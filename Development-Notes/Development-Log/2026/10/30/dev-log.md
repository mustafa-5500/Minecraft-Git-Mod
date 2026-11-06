**Date:** 2026-10-30
Previous Entry: [2026-10-25](../25/dev-log.md)

**Developer:**
    - Mustafa Faheem

**Comments:**
_New to dev logs? Use this section to explain your thought process - decisions made, tradeoffs, or context that isn't obvious from the code. Future developers (including future you) will thank you._

Continuing review of the Git implementation, commit by commit.

**Question:** In `SnapshotService.capture()` there is two main sections, one which captures block and entity data per block position and stores it in the block map, cache. The second part constructs a list of entity snapshots, for all non-player entities in a selection. Why do both sections exist? #Question
- Perhaps the entities captured in the first section are block entities, like item frames. While the entities captured in the second section are any entity, so non-blocks which are not constrained to the grid. This is demonstrated through the use of the Minecraft continuous `AABB` (Axis Aligned Bounding Box) data structure.

We have been adding doc strings above functions, moving both along function calls and chronologically along the commit history.


_Note: You can add `~~` at the start and end of a line to cross it out; So if an action or goal is complete, crossing it out will communicate that to other developers._
**Goals:**
    - _Longer-term objective(s) this work is building toward. These can span multiple days - leave them in place until complete._
	- Review the Bootstrap Commit

**Actions:**
    - _Concrete step(s) you're taking today toward the goal(s) above. Add one bullet per action, and cross items out with `~~like this~~` once done._
    - Review [GitServices](../../../../../Documentation/client/java/org/almond/minecraftgit/client/GitServices.md)
    - 