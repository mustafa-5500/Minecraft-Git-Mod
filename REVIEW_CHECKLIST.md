# Review checklist

- [ ] `Documentation/Testing.md` — Adds the confirmation prompt and cancel behavior to the manual acceptance checklist.
- [ ] `Documentation/evidence/wand-confirm-after-confirmed.png` — Changed-revision capture after confirming the pending cuboid.
- [ ] `Documentation/evidence/wand-confirm-after.png` — Changed-revision capture of the new confirmation prompt.
- [ ] `Documentation/evidence/wand-confirm-before.png` — Baseline capture showing the cuboid applied immediately with no prompt.
- [ ] `README.md` — Documents the confirm/cancel flow and the two new commands.
- [ ] `src/client/java/org/almond/minecraftgit/client/command/GitCommand.java` — Lists `confirm` and `cancel` in `/git help`.
- [ ] `src/client/java/org/almond/minecraftgit/client/command/SelectionCommands.java` — Adds `/git selection confirm` and `/git selection cancel` and updates pending-cuboid handling in create/add.
- [ ] `src/client/java/org/almond/minecraftgit/client/selection/SelectionManager.java` — Tracks which selection a pending cuboid was built for.
- [ ] `src/client/java/org/almond/minecraftgit/client/selection/WandEvents.java` — Shows a clickable confirmation prompt instead of immediately applying a completed wand cuboid.
