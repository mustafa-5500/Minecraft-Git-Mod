**Date:** 2026-10-24
Previous Entry: [2026-10-23](../23/dev-log.md)

**Developer:**
    - Mustafa Faheem

**Comments:**
Implemented the client-side counterpart of the Minecraft Git plugin as a Fabric 1.21 mod. The
plugin's plan was used as the baseline, with the server assumptions replaced by client-side ones:
Fabric client commands, `ClientLevel`/integrated-server capture, a vanilla custom-data wand instead
of a registered item, and single-player-only world writes.

The repository format is intentionally shared with the plugin: `<selection>/y_<y>/<quadrant>.csv`,
`entities.csv`, `meta.json`, and the same init commit marker. Block entities use native SNBT with an
`nbt:` prefix instead of the plugin's Paper-API JSON because the client can round-trip full NBT;
plugin JSON payloads are skipped with a warning while their block states still apply.

JGit 6.10 and JavaEWAH are nested in the mod jar with Loom's `include`, so no external Git binary is
needed. Work was split into `feature/bootstrap`, `repo-lifecycle`, `selection`, `snapshot`,
`staging`, `commit`, `status`, `sync-apply`, and `docs` worktrees; each was squashed to one commit
and merged into local `develop` with `--no-ff`, keeping the branch names.

**Goals:**
    - ~~Bootstrap a client-only Fabric mod with a working `/git` command shell.~~
    - ~~Port the plugin's JGit repository lifecycle, metadata, remotes, log, push, and fetch.~~
    - ~~Implement named multi-cuboid selections, a client wand, persistence, and particle previews.~~
    - ~~Implement block/block-entity/entity capture and the shared quadrant CSV format.~~
    - ~~Implement durable staging, commits, commit metadata, and coordinate diffs.~~
    - ~~Implement `/git status` and `/git diff`.~~
    - ~~Implement fast-forward pull, checkout, pre-apply backups, and `--abort` in single-player.~~
    - ~~Document the format, build/release flow, and manual test checklist.~~
    - Keep the apply/round-trip checklist green in a real client window.

**Actions:**
    - ~~Replaced the example mod with `org.almond.minecraftgit`, a client-only entrypoint, config, and a client command tree.~~
    - ~~Added JGit/JavaEWAH as bundled dependencies and verified the nested jars and startup class loading.~~
    - ~~Ported the JGit facade and added real-repository tests for init, commit, push, fetch, and pull planning.~~
    - ~~Added selections, wand events, particle rendering, active-selection persistence, and selection commands.~~
    - ~~Added native block-state/NBT/entity codecs and quadrant CSV writer/reader tests.~~
    - ~~Added gzip staging, `/git add`/`/git rm`, cuboid-trim, and reorigin rebasing.~~
    - ~~Added commit creation, `meta.json`, `BuildDiff`, `/git commit`, `/git status`, and `/git diff`.~~
    - ~~Added pull/checkout planning, integrated-server apply in tick batches, backups, and `--abort`.~~
    - ~~Rewrote README/DEVOPS and added `Documentation/Git-Format.md` and `Documentation/Testing.md`.~~
    - Run the full manual apply/checkout/abort round trip on a windowed client.

**Notes:**
    - Applying builds requires single-player; multiplayer is intentionally capture/commit/remote-only.
    - `/git pull` is fast-forward-only; diverged histories are reported rather than merged.
    - Status capture has a configurable volume cap and no TTL cache yet.
    - Backups currently cover blocks and block entities, not entities.
    - Full vanilla NBT and entity behavior still need a real in-world test; the codecs compile and
      the client starts offline, but this environment cannot download assets or join a world.
