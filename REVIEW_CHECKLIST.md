# Review Checklist

- [ ] `src/client/java/org/almond/minecraftgit/client/selection/SelectionManager.java` — Exposes the already-clicked corner of the cuboid currently being created.
- [ ] `src/client/java/org/almond/minecraftgit/client/selection/SelectionRenderer.java` — Draws the live in-progress outline and moves every outline particle onto the outer block faces so solid terrain cannot hide it.
- [ ] `Documentation/evidence/wand-preview-before.png` — Actual client capture of the baseline (`develop`, 511c8e2): no outline while the cuboid is being selected.
- [ ] `Documentation/evidence/wand-preview-after.png` — Actual client capture of this change: the in-progress outline is visible.
