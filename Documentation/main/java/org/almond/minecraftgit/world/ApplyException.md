# ApplyException

> **Software Detailed Documentation:** [ApplyException_SDD.md](./ApplyException_SDD.md)  
> **Source File:** [ApplyException.java](../../../../../../../src/main/java/org/almond/minecraftgit/world/ApplyException.java)

## Table of Contents

- [Usage](#usage)
- [ApplyException (Constructor)](#applyexception-constructor)
- [See Also](#see-also)

## Usage

Throw an `ApplyException` when an apply cannot start or continue safely, and complete the returned future exceptionally with it; the command layer unwraps the future and shows the message in chat. `ApplyService` uses it for a missing integrated server, a stopping server, and a failed pre-apply backup capture or write.

```java
if (server == null || level == null) {
    return CompletableFuture.failedFuture(new ApplyException(
            "Applying a build requires a single-player world (integrated server). "
                    + "Repository commands still work in multiplayer."));
}
try {
    backup = snapshots.capturePositions(level, plan.selectionName(), plan.targetOrigin(),
            plan.allPositions(), true);
} catch (RuntimeException e) {
    future.completeExceptionally(new ApplyException(
            "Could not capture a pre-apply backup: " + e.getMessage(), e));
}
```

A world apply could not proceed safely.

---

## ApplyException (Constructor)

### Signature
```java
public ApplyException(String message)
```

### Description
Creates an exception whose message is safe to show to the player, with no underlying cause. `ApplyService` uses this form for precondition failures such as a missing integrated server, one that is stopping, or one that stopped mid-apply.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Human-readable explanation of why the apply cannot proceed, shown in chat unchanged. |

### Returns
`ApplyException` instance.

---

## ApplyException (Constructor)

### Signature
```java
public ApplyException(String message, Throwable cause)
```

### Description
Creates an exception that keeps the originating failure for diagnostics while exposing a friendly summary to the player. `ApplyService` uses it when wrapping a backup capture or backup write failure, so the cause remains available to logs but the chat message stays actionable.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `message` | `String` | Human-readable summary of the failed apply step. |
| `cause` | `Throwable` | Underlying exception from snapshot capture, backup IO or another apply step. |

### Returns
`ApplyException` instance.

---

## See Also

- **Software Detailed Design:** [ApplyException_SDD.md](./ApplyException_SDD.md)
