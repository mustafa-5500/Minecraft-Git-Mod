# MinecraftGitClient

> **Software Detailed Documentation:** [MinecraftGitClient_SDD.md](./MinecraftGitClient_SDD.md)  
> **Source File:** [MinecraftGitClient.java](../../../../../../../src/client/java/org/almond/minecraftgit/client/MinecraftGitClient.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [onInitializeClient](#oninitializeclient)
- [services](#services)
- [See Also](#see-also)

## Usage

Fabric constructs this class by name from `fabric.mod.json` and calls `onInitializeClient()` once during client startup; code elsewhere never instantiates it and instead calls `MinecraftGitClient.services()`. For example, `GitServices services = MinecraftGitClient.services();` is how a command or screen obtains the graph. After the client stops, the static reference is null and `services()` throws.

Client entrypoint. All commands, world access and rendering are client-side;
the mod is marked `"environment": "client"` and has no server
entrypoint.

---

## Fields

- `services` ([`GitServices`](GitServices.md)) — Static reference to the single live `GitServices`; set in `onInitializeClient()` and nulled after `close()` on `CLIENT_STOPPING`, so `services()` can detect use after shutdown.

---

## onInitializeClient

### Signature
```java
@Override
public void onInitializeClient()
```

### Description
Loads `config.json` from `<config dir>/minecraftgit`, builds the `DebugLogger` and the `GitServices` graph, registers the `/git` command tree, the wand block callbacks and the selection renderer, and registers a `CLIENT_STOPPING` hook that closes the graph. It also probes for `org.eclipse.jgit.api.Git` and logs whether JGit is present, without aborting startup when it is missing.

### Parameters
None.

### Returns
`void`

---

## services

### Signature
```java
public static GitServices services()
```

### Description
Returns the live service graph, throwing `IllegalStateException("Minecraft Git is not initialized.")` when called before initialization or after `CLIENT_STOPPING`.

### Parameters
None.

### Returns
[`GitServices`](GitServices.md) — the single live service graph.

---

## See Also

- **Software Detailed Design:** [MinecraftGitClient_SDD.md](./MinecraftGitClient_SDD.md)
