# SelectionException

> **Software Detailed Documentation:** [SelectionException_SDD.md](./SelectionException_SDD.md)  
> **Source File:** [SelectionException.java](../../../../../../../src/main/java/org/almond/minecraftgit/selection/SelectionException.java)

## Table of Contents

- [Usage](#usage)
- [SelectionException (Constructor)](#selectionexception-constructor)
- [See Also](#see-also)

## Usage

Thrown by `SelectionService` and snapshot/wand code when the player can correct the problem, such as an unknown name, a duplicate, an invalid index or an exceeded cap; commands catch it and print its message directly. For example, `services.selections().get(worldId, owner, name)` throws `SelectionException` with `No selection named '...'.` when the name is unknown.

A selection or wand operation the user can correct.

---

## SelectionException (Constructor)

### Signature
```java
public SelectionException(String message)
```

### Description
Creates a user-facing selection failure carrying the exact chat message to show; the class adds no fields or behaviour of its own.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Human-readable explanation shown verbatim in chat. |

### Returns
`SelectionException` instance.

---

## See Also

- **Software Detailed Design:** [SelectionException_SDD.md](./SelectionException_SDD.md)
