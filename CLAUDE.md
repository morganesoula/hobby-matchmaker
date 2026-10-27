# CLAUDE.md

HobbyMatchMaker is a Kotlin Multiplatform (KMP) app targeting Android and iOS — a social
hobby-matching platform built on Clean Architecture + Compose Multiplatform. Movies is the first
hobby implemented; TV shows, activities, and others will follow.

---

## Skills — use these before implementing anything

| Scenario                                                                      | Skill                 |
|-------------------------------------------------------------------------------|-----------------------|
| Setting up / structuring a module, convention plugins, where does X live      | `/kmp-architecture`   |
| Creating or reviewing a ViewModel, screen state, EventModel, Orchestrators    | `/kmp-ui-pattern`     |
| Writing composables, UI components, UiState, FormState, design system         | `/kmp-compose`        |
| Data sources, repositories, DTOs, mappers, SQLDelight, Ktor, Firestore        | `/kmp-data-layer`     |
| SQL tables/queries, Firestore collections/fields, Storage paths, transactions | `/kmp-database`       |
| Koin modules, providing dependencies, ViewModel injection, KoinInit           | `/kmp-di`             |
| Adding a route, navigating between screens, NavigationDestination, AppNavHost | `/kmp-navigation`     |
| AppError, AppResult, safeCall wrappers, error mapping, UIErrorHint            | `/kmp-error-handling` |
| Reviewing changed code for quality, reuse, and efficiency                     | `/simplify`           |
| Modifying settings.json, hooks, permissions, env vars                         | `/update-config`      |
| Pushing the Android app online / Google Play release, version bump, .aab      | `/play-store-release` |

---

## Where to find things

| What                                     | Where                                                                                                                                                                                                                                                                                    |
|------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Module layout                            | `core/` and `features/` at project root                                                                                                                                                                                                                                                  |
| Design system & components               | `core/design/src/commonMain/`                                                                                                                                                                                                                                                            |
| Navigation routes                        | `core/navigation/presentation/src/commonMain/kotlin/presentation/`                                                                                                                                                                                                                       |
| DI wiring entry point                    | `composeApp/src/commonMain/kotlin/.../presentation/navigation/KoinInit.kt`                                                                                                                                                                                                               |
| Database schema (SQLDelight + Firestore) | `core/database/src/commonMain/sqldelight/` + see `/kmp-database` skill                                                                                                                                                                                                                   |
| Feature READMEs                          | `features/<name>/README.md`                                                                                                                                                                                                                                                              |
| Mockups                                  | `features/<name>/mockup/` -  each screen has a `.png` (visual intent) and a Stitch `.html`. Read both before implementing a screen. Map HTML colors/spacing/typography to `core/design` tokens and components - never hardcode them. If they conflict, the `.png` wins. |
| Build commands                           | Read `build.gradle.kts` or module `build.gradle.kts` as needed                                                                                                                                                                                                                           |


---

## Git

**DO NOT** add the Claude co-author line in any commit messages.
Commit format: feat (scope): message --in-progress/--finished.
Use keywords such as `add, clean, fix, migrate, ui improvements` in your git messages.

---

## Other

**DO NOT** try to run the app on a device or emulator after implementing, updating or deleting code.
