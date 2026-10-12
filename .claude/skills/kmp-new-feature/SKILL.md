---
name: kmp-new-feature
description: |
  End-to-end, ordered checklist for adding a new feature to HobbyMatchmaker KMP (e.g. favorites, TV shows, activities) — from README/mockup to Gradle modules, settings.gradle.kts, Versions.kt Modules, composeApp and navigation dependencies, Koin modules + KoinInit, Route, AppNavHost, and the screen itself. It orchestrates the other kmp-* skills and points to them for each step. Use this skill whenever creating a new feature or a new feature screen that needs its own module. Trigger on phrases like "new feature", "create a feature", "add a feature module", "start the favorites screen", "scaffold a feature", "new hobby", or "wire a new screen end-to-end".
---

# New Feature — End-to-End Checklist

A new feature touches every layer. This skill gives the **order** and the **exact files**; for *how* to write
each piece, load the skill named in the step. Do the steps in order — later steps depend on earlier ones
compiling.

Throughout: `<name>` = lowercase feature name (`favorites`), `<Name>` = PascalCase (`Favorites`),
`<NAME>` = constant prefix (`FAVORITES`).

Reference implementation to copy from: `features/social/` (full data/domain/presentation).
`features/hub/` is the reference for a feature **without** a data layer.

---

## 0. Specs first — stop if missing

- [ ] `features/<name>/README.md` exists and describes the functional logic (same shape as `features/movies/README.md`)
- [ ] `features/<name>/mockup/` has a `.png` + Stitch `.html` per screen

If either is missing, **ask the user** before writing code — do not invent the feature's behaviour or design.

## 1. Decide the layers — `/kmp-architecture`

- [ ] Which layers have real content? `domain` almost always; `data` only if the feature owns a repository;
  `presentation` if it has a screen. Omit empty layers (like `hub`, which has no `data`).
- [ ] Check what can be reused from other features' `domain` modules — features never depend on another
  feature's `data` or `presentation`.
- [ ] Need new storage? Plan the SQL table / Firestore collection with `/kmp-database` now.

## 2. Gradle modules — `/kmp-architecture`

- [ ] `features/<name>/build.gradle.kts` (parent) containing only `plugins { base }`
- [ ] One folder per layer with its `build.gradle.kts`, copied from the matching `features/social/<layer>/build.gradle.kts`:
  - `domain` → `alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)` (this is what every existing domain module uses)
  - `data` → `alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)` + `multiplatformConfig { useFirebase() }` if it talks to Firebase
  - `presentation` → `alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)`
  - add `alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)` if the layer will have tests
- [ ] `android { namespace = "com.msoula.hobbymatchmaker.features.<name>.<layer>" }` in each layer
- [ ] Source root: `src/commonMain/kotlin/com/msoula/hobbymatchmaker/features/<name>/<layer>/`

## 3. Register the modules — 4 files, don't skip any

- [ ] `settings.gradle.kts` → `include(":features:<name>")` + one `include(":features:<name>:<layer>")` per layer
- [ ] `build-logic/convention/src/main/java/Versions.kt` → `<NAME>_DOMAIN`, `<NAME>_DATA`, `<NAME>_PRESENTATION` in `object Modules`
- [ ] `composeApp/build.gradle.kts` → `implementation(project(Modules.<NAME>_*))` for **every** layer (composeApp wires DI)
- [ ] `core/navigation/presentation/build.gradle.kts` → `implementation(project(Modules.<NAME>_PRESENTATION))` (AppNavHost renders the screen)
- [ ] Layer dependencies in each `commonMain.dependencies` respect the dependency table in `/kmp-architecture`
  (`data` and `presentation` depend on own `domain`; `domain` never on `data`/`presentation`)

✅ Checkpoint: Gradle sync / `./gradlew :features:<name>:domain:compileAndroidMain` succeeds before writing feature code.

## 4. Domain — `/kmp-data-layer`, `/kmp-error-handling`

- [ ] `*DomainModel`s in `domain/models/`
- [ ] `*Repository` interface in `domain/repositories/`, returning `AppResult` / `Flow`
- [ ] Use cases (`GetX`, `ObserveX`, `SetX`, `IsX`/`HasX` for booleans) in `domain/useCases/`

## 5. Data (if any) — `/kmp-data-layer`, `/kmp-database`, `/kmp-error-handling`

- [ ] SQL: queries in `core/database/.../hmm_database.sq`, DAO interface + impl in `:core:database`, registered in `coreModuleDAO`
- [ ] Firestore/Storage: collection and field names per `/kmp-database`
- [ ] `*LocalDataModel` / `*RemoteDataModel` + mappers
- [ ] `*LocalDataSourceImpl` (`safeCallStorage`) / `*RemoteDataSourceImpl` (`safeNetworkCall`)
- [ ] `*RepositoryImpl` implementing the domain interface (SQLDelight stays the UI source of truth)

## 6. Presentation — `/kmp-ui-pattern`, `/kmp-compose`

- [ ] `*EventModel` sealed interface, `*ViewModel` (multiple `StateFlow`s, `externalScope`), `*Orchestrator` if several use cases
- [ ] `@Immutable *UiModel` with `Initial`, `DomainModel.toUiModel()` mapper
- [ ] `*Screen` / `*Content` composable built from `core/design` components and tokens, matching the mockup (`.png` wins over `.html`)
- [ ] User-visible strings as `Res.string.*` in `core/design/src/commonMain/composeResources/`

## 7. Dependency injection — `/kmp-di`

- [ ] `featuresModule<Name>Domain` in `domain/.../di/`
- [ ] `featuresModule<Name>Data` in `data/.../di/` (if data layer)
- [ ] `featuresModule<Name>Presentation` in `presentation/.../di/` (`viewModel { … }`)
- [ ] Import and add all of them to `appModule()` in
  `composeApp/src/commonMain/kotlin/com/msoula/hobbymatchmaker/presentation/navigation/KoinInit.kt`

## 8. Navigation — `/kmp-navigation`

**ONLY** if needed. Sometimes, you will add new feature inside an existing screen. No route should be added then.

- [ ] `@Serializable` route in `core/navigation/presentation/src/commonMain/kotlin/presentation/Route.kt`
- [ ] `NavigationDestination` entry — only if a ViewModel triggers the navigation
- [ ] `NavigationCallbacks` entry
- [ ] `composable<<Name>>` block in `AppNavHost.kt` (or `MainScaffold` if it's a tab) with `koinViewModel()` and a
  `LaunchedEffect(Unit)` collecting `vm.events`
- [ ] Entry point from an existing screen (button, tab, top-bar icon…) as described in the README

## 9. Verify

- [ ] `./gradlew detektAll -PdetektFiles=$(git diff --name-only --diff-filter=d HEAD -- '*.kt' | paste -sd, -)`
  (untracked new files are not in `git diff` — add them with `git ls-files --others --exclude-standard -- '*.kt'`)
- [ ] Compile Android: `./gradlew :composeApp:compileAndroidMain` (iOS: `:composeApp:compileKotlinIosSimulatorArm64`)
- [ ] Re-read sections 3 and 7: a missing `settings.gradle.kts`, `composeApp` dependency or `appModule()` entry
  is the most common failure and only shows at runtime for Koin
- [ ] Update `.claude/skills/kmp-architecture/SKILL.md` module layout table with the new modules
