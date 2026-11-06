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

TODO: Add usage examples for `MinecraftGitClient`.

Client entrypoint. All commands, world access and rendering are client-side;
the mod is marked {@code "environment": "client"} and has no server
entrypoint.

---

## Fields

- `services` ([`GitServices`](GitServices.md)) — TODO: describe field

---

## onInitializeClient

### Signature
```java
@Override
public void onInitializeClient()
```

### Description
TODO: Describe what `onInitializeClient` does.

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
TODO: Describe what `services` does.

### Parameters
None.

### Returns
[`GitServices`](GitServices.md) — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [MinecraftGitClient_SDD.md](./MinecraftGitClient_SDD.md)
