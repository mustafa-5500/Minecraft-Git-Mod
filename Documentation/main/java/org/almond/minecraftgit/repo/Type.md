# Type

> **Software Detailed Documentation:** [Type_SDD.md](./Type_SDD.md)  
> **Source File:** [PullPlan.java](../../../../../../../src/main/java/org/almond/minecraftgit/repo/PullPlan.java)

## Table of Contents

- [Usage](#usage)
- [PullPlan](#pullplan)
- [canFastForward](#canfastforward)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `Type`.

TODO: Add description for `Type`.

---

## PullPlan

### Signature
```java
public record PullPlan(String remote, String branch, ObjectId localHead, ObjectId remoteHead,
                       ObjectId mergeBase, Type type, int fetchedRefs)
```

### Description
A fetch plus the relationship between local and remote branches. The caller
may apply the remote snapshots to the world before {@link RepoService#fastForward}
moves the local branch, so a failed world write never leaves Git ahead of the
world.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `remote` | `String` | TODO: describe |
| `branch` | `String` | TODO: describe |
| `localHead` | `ObjectId` | TODO: describe |
| `remoteHead` | `ObjectId` | TODO: describe |
| `mergeBase` | `ObjectId` | TODO: describe |
| `type` | `Type` | TODO: describe |
| `fetchedRefs` | `int` | TODO: describe |

### Returns
`record` — TODO: describe return value.

---

## canFastForward

### Signature
```java
public boolean canFastForward()
```

### Description
TODO: Describe what `canFastForward` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [Type_SDD.md](./Type_SDD.md)
