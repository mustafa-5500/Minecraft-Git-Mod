**Motivation:** Most of this client side mod was created using AI, so we need to review the code to make sure the implementation is correct, check for any concerns with bugs, security, or features.

**Idea:** Since we made the AI Commit each feature it was implementing, we can follow the commit history to see what was added.

---
## Commit History
### `feature/bootstrap`
#### `build.gradle` Changes
**Repositories:** The dependencies for Jgit come from Maven Central

**Dependencies:** The Jgit repository is implemented into the mode and included as a dependency along with Jgit's JavaEWAH dependency. Furthermore Junit is added for automated testing.

#### Tasks
**`tests`:** Uses the Junit dependency to run the automated testing.

#### `gradle.properties` Changes
The maven group and mod name are applied, and the `Jgit`, `JavaEWAH`, and `Junit` version numbers are recorded.

**Question:** For future versioning changes will we need to update the dependency versions manually or are they done by gradle and the added gradle plugins?
- They will need to be updated manually, so we need to find out software versioning and compatibility to create different version of the mod.

#### `settings.gradle` Changes
