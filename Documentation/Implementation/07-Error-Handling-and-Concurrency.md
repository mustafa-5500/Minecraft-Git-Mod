# 7. Error Handling and Concurrency

This chapter collects the rules that are easy to miss when reading a single
class: how failures are represented, which thread owns which state, and in what
order side effects happen.

## 7.1 Exception hierarchy

`UserFacingException` is the base for errors that are safe and useful to show in
chat. Its concrete subclasses are:

| Exception | Thrown by | Meaning |
|-----------|-----------|---------|
| `SelectionException` | `SelectionService`, commands | Missing/duplicate selection, cuboid count/volume cap, no cuboids/origin, corrupt selection store. |
| `StagingException` | `StagingService`, `SnapshotStore` | Missing/corrupt/truncated `.snap`, unwritable staging, string/array bound violation. |
| `CommitException` | `CommitService` | Nothing staged, selection not staged, nothing changed, temp-tree failure. |
| `PullException` | `PullService`, `PullCommands` | Pull/checkout precondition, diverged history, partial pull, unknown commit/selection, no backup. |
| `BackupException` | `BackupService` | Backup creation/list/prune failure. |
| `ApplyException` | `ApplyService` | Not single-player, server not available/stopping, backup capture/write failure, stopped mid-apply. |
| `RepoException` | `RepoService`, `RepoMetadataStore` | JGit/transport/config/export failure, invalid remote, path escape, detached HEAD. |

Other exceptions are not user-facing:

- `IllegalArgumentException` — programming/argument errors (null cuboid, invalid
  selection name, blank author). `CommandSupport.handleError` still prints its
  message for command callers.
- `SerializationException` — corrupt/unrecognized CSV/NBT/Base64 data; usually
  wrapped by a user-facing exception or logged by status/diff.
- `SelectionException`/`RepoException` during `readHead` are caught by
  `StatusService.readHead`, logged, and treated as "no HEAD" so a damaged
  selection does not break the whole status output.
- Any other `Throwable` is logged by `CommandSupport.handleError` and summarized
  as `"Git operation failed: <message> (see the game log for details)"`.

### Error translation

`RepoService.error(action, cause)` builds `"Could not <action>: <friendly>"` and
logs the full cause. `friendlyMessage` maps transport failures to short,
actionable text:

- auth/401/403 -> "Authentication failed. Check the remote URL and credentials."
- unknown host/connection refused/timeout/network unreachable -> "Could not reach
  the remote. Check the URL and your connection."
- not found/repository not found -> "The remote repository was not found."
- otherwise the original message.

`CommandSupport.unwrap` strips `CompletionException`/`ExecutionException`
wrappers before dispatching on the cause.

## 7.2 The command error path

Every command handler returns through one of:

- `CommandSupport.run(source, Runnable)` — run now, catch, print, return 1/0.
- `CommandSupport.runInt(source, Supplier<Integer>)` — same, returns the
  supplier's int.
- `CommandSupport.complete(services, source, Supplier<T>, Consumer<T>)` — run
  `supplier` on the IO executor; on completion hop to the client thread, drop the
  result if the player left, otherwise call `success` or `handleError`.
- `CommandSupport.completeFuture(source, future, Consumer<T>)` — same for an
  already-running future (used by apply).

`handleError` prints `UserFacingException`/`IllegalArgumentException` messages
without a stack hint and logs everything else. A failure inside the success
consumer is also routed through `handleError`, so a formatting bug cannot escape
into the event loop.

## 7.3 Thread ownership

| State | Owner | Notes |
|-------|-------|-------|
| `Minecraft`/`ClientLevel`/`ClientPlayer` | client thread | Only client-thread code touches it; `WorldAccess.captureOnClientThread` hops if needed. |
| `ServerLevel`/`MinecraftServer` | integrated-server thread | Captures/applies submitted with `server.execute`; never touched from IO. |
| `SelectionManager.sessions` | client thread (conceptually) | `ConcurrentHashMap` for safety, but sessions are read/written from commands/events. |
| `SelectionService.cache` | callers (client + IO) | `ConcurrentHashMap`; mutation methods persist synchronously. |
| `RepoMetadata` cache | callers (client + IO) | `ConcurrentHashMap`; `RepoMetadataStore.save` atomic. |
| JGit `Git`/`Repository` | `RepoService.lock` | All public `RepoService` methods take the `ReentrantLock`; JGit is not thread-safe. |
| `CommitService` read/diff/write sequence | `CommitService.commitLock` | Prevents interleaved commits even if callers race. |
| Staged `.snap` files | `StagingService` callers | No file lock; commands serialise through the IO executor/commit lock in practice. |
| `DebugLogger.writer` | `synchronized log/close` | Buffered; flush every 32 lines; one failure warning. |
| `SelectionRenderer` static state | client thread | Highlight state and tick counter. |

### Thread-hop rules

1. **Never block the client thread.** JGit, CSV writing, staging reads/writes and
   backup IO go through `GitServices.async`/`asyncRun` (the 2-thread pool) or
   `CommandSupport.complete`.
2. **Never touch a `Level` from IO.** `WorldAccess` and `ApplyService` are the
   only places that bridge world access; they always submit to the owning thread
   (`server.execute` for integrated server, `client.execute`/current thread for
   the client level).
3. **Never call chat APIs from the server/IO thread.** Future callbacks are
   marshalled back with `client.execute` in `CommandSupport`/`PullCommands`.
4. **Do not capture after the player left.** `CommandSupport.complete` checks
   `client.player`/`client.level` before invoking the consumer and silently drops
   the result otherwise.

### Apply job re-scheduling

`ApplyService.Job.process` writes at most `config.blocksPerTick` blocks and then
re-submits itself with `server.execute(this::process)` while work remains. This
keeps the integrated server responsive; the `future` completes only in
`finish()`, after entities are handled. If `server.isRunning()` becomes false
mid-job, the future fails with `ApplyException("The integrated server stopped
during apply.")`.

## 7.4 Locking and ordering guarantees

The system relies on a few ordering rules rather than on broad locking:

1. **Commit is atomic from the caller's perspective.** `commitLock` covers
   reading old HEAD, diffing, writing the temp tree and calling
   `commitWorkspace`. `RepoService` then holds its own lock while deleting/writing
   the worktree, staging and committing.
2. **Pull applies before it moves the branch.** `PullCommands` calls
   `RepoService.fastForward` only after all `ApplyService.apply` futures succeed.
   A failed apply leaves HEAD at the old commit.
3. **Abort restores the latest backup.** `BackupService.latest` sorts by file name
   (`millis-sequence.snap`); `ApplyService` writes a *new* backup before restoring,
   so an abort is itself undoable.
4. **Selection mutation writes through immediately.** `SelectionService` persists
   on every mutation, so a crash between commands cannot lose a selection
   definition. `SelectionManager` state (corners/pending) is intentionally
   in-memory and may be lost.
5. **Staging writes are atomic.** `SnapshotStore.write` writes a `.tmp` sibling
   and moves it with `ATOMIC_MOVE` (fallback: replace). A reader never sees a
   partial `.snap`.
6. **Config/metadata writes are atomic.** `ModConfig.save`,
   `SelectionMetadataStore.save`, `RepoMetadataStore.save` and
   `CommitMeta.write` follow the same temp-then-move pattern (the commit meta
   write is to a fresh temp tree).
7. **`RepoService` path checks precede destructive writes.** `safeResolve`
   rejects paths outside the repository; `exportSelectionAt` rejects exported
   paths that escape the temp target.

## 7.5 Partial results and warnings

Several operations can succeed partially. They return a result with warnings
rather than throwing:

- `CaptureResult.partial()` — unloaded chunks, skipped blocks outside build
  height, capped entities. `/git add` still stages the snapshot; `status` marks
  the working diff as incomplete; `/git pull`/`checkout` apply what was read.
- `ApplyResult.warnings()` — unknown block states (applied as air), out-of-height
  coordinates, failed block writes, unsupported block-entity payloads, failed
  entity restores. The command prints the first 5.
- `BackupService.store` — a backup is best-effort for a single broken block
  entity (`capturePositions` catches payload encode failures and stores the block
  without it).
- `StatusService` — a corrupt HEAD selection is logged and treated as absent; a
  staging read failure is logged and treated as not staged; a too-large or
  incomplete selection gets a note instead of a working diff.

The design principle is: **read failures degrade the report; write failures fail
the operation.** A capture that misses unloaded chunks is still useful and is
reported; a staging/commit/apply failure is a real error.

## 7.6 Input and resource limits

Limits are enforced at several layers:

| Limit | Where | Effect |
|-------|-------|--------|
| Cuboids per selection | `SelectionService.addCuboid` | `SelectionException` |
| Selection volume | `SelectionService.addCuboid` | `SelectionException` |
| Capture volume | `SnapshotService.capture` (`maxBlocksPerCommit`) | `SelectionException` |
| Entities per capture | `SnapshotService.captureEntities` | cap + warning + `skippedEntities` |
| Block-entity/entity payload bytes | `SnapshotService`, `ApplyService` | payload skipped + warning |
| Status volume | `StatusCommands` (`statusMaxVolume`) | selection listed as too large, no working diff |
| Staging file bounds | `SnapshotStore` | `StagingException` |
| Apply write batch | `ApplyService` (`blocksPerTick`) | yields between ticks |
| Backups kept | `BackupService.prune` | oldest deleted |
| Render particles | `SelectionRenderer` (`renderBudget`) | stops drawing |
| Selection names | `Names` | validated, never sanitised silently |
| Remote names | `RepoService.requireValidRemoteName` | `RepoException` |

## 7.7 Security and safety notes

- **Path traversal:** selection names are validated to a restricted character
  set before becoming paths; repository writes go through `safeResolve`; commit
  exports go through a target-prefix check. World ids are sanitised because the
  player never types them.
- **No credentials in the repository:** remotes are URLs; the mod does not add
  credentials, and JGit's auth comes from its own transport configuration. Debug
  logs can contain block coordinates and repository paths; they stay local.
- **World writes:** limited to single-player integrated servers, and only via
  `ApplyService`. A client-only mod cannot and does not write to a remote
  server's world.
- **No server entrypoint:** `fabric.mod.json` has `"environment": "client"`, so
  the mod does not load on a dedicated server.
- **NBT parsing:** block-entity/entity SNBT is parsed with Minecraft's
  `TagParser`; invalid payloads throw `SerializationException` and the apply
  reports a warning/skips the payload rather than silently corrupting a block.

## 7.8 Known sharp edges

These are real behaviours a maintainer should know about:

1. **A partial multiplayer capture only sees loaded chunks.** It is not marked
   fatal; the player may not notice missing blocks until status/diff.
2. **A pull into a world with existing cuboids keeps them.** If the remote build
   extends outside the local cuboids, those extra blocks are not tracked; grow
   the selection and pull again. `PullCommands.addCuboids` only adds cuboids when
   the local selection has none.
3. **A partial pull is refused when the remote has several selections.** A
   fast-forward moves the branch to the entire remote tree; applying only one
   selection would leave the others in HEAD but not in the world.
4. **The branch is moved after apply, not before.** A crash between them leaves
   the world changed but HEAD old; `/git pull --abort` restores the backup, then
   retry.
5. **Abort does not restore entities.** Backups capture blocks and block
   entities only.
6. **A failed `apply` may have written some blocks.** The future counts
   placed/removed and reports warnings; the backup is the recovery path.
7. **`/git commit` deletes staged files even for unchanged selections.** They were
   overwritten by `/git add`; the old commit data is untouched.
8. **The status volume cap is per selection**, not per command; a status over
   many small selections can still take time.
9. **`RepoService` serialises all repository work for one world.** Two worlds
   can use the two IO threads, but one world's fetch/commit blocks its own
   subsequent repository commands.
10. **Debug block logging is huge.** `debugBlocks` is opt-in and should only be
    used for a bounded repro.
