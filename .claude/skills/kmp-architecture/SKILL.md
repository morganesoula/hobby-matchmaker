---
name: kmp-architecture
description: Module layout, dependency rules, and Gradle convention plugins for Android and Kotlin Multiplatform (KMP) projects. Use this skill whenever setting up a new Android/KMP project, deciding where a new module should live, asking "how should I structure this", creating a new feature module, adding a core submodule, configuring Gradle convention plugins, working with version catalogs, or making any decision about project-level architecture. Trigger on phrases like "set up the project", "add a module", "create a feature", "how should I structure", "project structure", "convention plugin", "build-logic", or "where does X live".
---

# Android / KMP Module Structure

## Core Philosophy

- **Feature-layered modularization**: split by feature first, then by layer within each feature.
- **Clean Architecture layers**: `presentation` → `domain` ← `data`. Domain is innermost and depends on nothing.
- **Code lives in a feature module**.
- Features data and presentation **never depend on each other**. Cross-feature should be shared via the feature domain submodule.

---

## Active Targets

This project targets **Android and iOS only**:
- `androidTarget()` — via `com.android.kotlin.multiplatform.library`
- `iosArm64()` — physical iOS devices
- `iosSimulatorArm64()` — iOS Simulator (Apple Silicon)

No JVM, JS, or other targets are configured. iOS targets can be disabled during fast Android-only builds via `-Pkmp.buildTargets.ios.enabled=false`.

---

## Module Layout

```
:composeApp                         ← KMP shared app entry point (wires all modules, iOS framework)
:androidApp                         ← Android-only app shell (delegates to composeApp)
:build-logic                        ← Gradle convention plugins

:core:authentication:data           ← Authentication data models, repository implementation
:core:authentication:domain         ← Authentication domain models, repository interface (log out, sign in with e-mail, etc.)
:core:common                        ← Result, AppError, Dispatcher, Logger, utility
:core:database                      ← SQLDelight database creation, drivers, all entity definitions, adapters, all DAOs (interface & implementation)
:core:design                        ← Reusable Compose components, colors, theme, typography
:core:login:domain                  ← Login domain contracts
:core:login:presentation            ← Login screen validation, directing sign-in and sign-up
:core:navigation:presentation       ← AppNavHost, navigation callbacks, routes
:core:network                       ← Platform-specific network client (HTTPClient, Ktor)
:core:session:data                  ← Session persistence (DataStore)
:core:session:domain                ← Session domain contracts (authenticated state, uuid)
:core:splashscreen:presentation     ← Splash screen
:core:user:data                     ← Session user data (local + remote)
:core:user:domain                   ← Session user domain contracts

:features:hub:domain                ← Hub domain contracts and use cases
:features:hub:presentation          ← Hub UI (no dedicated data layer — reads from other features)
:features:moviedetail:data          ← Movie detail repository implementation, DTOs, mappers
:features:moviedetail:domain        ← Movie detail domain models, repo interfaces, use cases
:features:moviedetail:presentation  ← Movie detail ViewModel, composables, state
:features:movies:data               ← Movie list repository implementation, DTOs, mappers
:features:movies:domain             ← Movie list domain models, repo interfaces, use cases
:features:movies:presentation       ← Movie list ViewModel, composables, state
:features:profile:data              ← User profile repository implementation, DTOs, mappers
:features:profile:domain            ← User profile domain models, repo interfaces, use cases
:features:profile:presentation      ← User profile ViewModel, composables, state
:features:social:data               ← Social circle repository implementation, DTOs, mappers
:features:social:domain             ← Social circle domain models, repo interfaces, use cases
:features:social:presentation       ← Social circle ViewModel, composables, state
```

> Not every feature requires all three layers. `features:hub` has no `data` layer because it reads from other feature domains. Only add layers that have meaningful content.

For standalone, self-contained concerns with multiple classes or a non-trivial API surface, create a dedicated module under `:core` (e.g., `:core:location`, `:core:analytics`). Do not create a module for a single class or trivial utility — that belongs in an existing `core` module.

Feature modules that need DB access depend on `:core:database` directly; features that don't remain decoupled.

---

## Dependency Rules

| Layer | May depend on |
|---|---|
| `presentation` | `domain` (own feature), `<any-module>:domain`, `core:design`, `core:common`, `core:network` |
| `data` | `domain` (own feature), `<any-module>:domain`, `core:database`, `core:common`, `core:network` |
| `domain` | `<any-feature>:domain` only — never `data` or `presentation` |
| `:composeApp` | everything (wires all modules) |

---

## Convention Plugins (`:build-logic`)

Gradle convention plugins define common setup across all KMP modules using `com.android.kotlin.multiplatform.library`, Java 21 toolchain, and unified target configurations.

### Available Plugins

| Plugin ID | Class | Included Dependencies / Config | Target Modules |
|---|---|---|---|
| `hobbymatchmaker.buildlogic.multiplatformCompose` | `MultiplatformComposeConventionPlugin` | • Compose Multiplatform & Compiler<br>• Coroutines, Immutable Collections<br>• Compose UI / Material3 / Preview / Resources<br>• Koin Core, Koin Compose & Compose-ViewModel<br>• Compose Navigation<br>• Kotzilla SDK | `:features:*:presentation`<br>`:core:*:presentation`<br>`:core:design` |
| `hobbymatchmaker.buildlogic.multiplatform` | `MultiplatformConventionPlugin` | • Kotlin Serialization<br>• Coroutines<br>• Koin Core (`api`)<br>• Compose Navigation<br>• Kotzilla SDK | `:features:*:data`<br>`:core:*:domain`<br>`:core:*:data`<br>`:core:network`<br>`:core:database` |
| `hobbymatchmaker.buildlogic.multiplatformMinimalist` | `MultiplatformMinimalistPlugin` | • Pure base multiplatform + serialization plugin<br>• Koin Core (`api`) only | `:features:*:domain`<br>`:core:common` |
| `hobbymatchmaker.buildlogic.multiplatformTest` | `MultiplatformTestConventionPlugin` | • Kotest (Framework Engine + Assertions)<br>• Turbine<br>• Coroutines Test | Any module requiring unit/flow tests |

---

### Configuration Extension (`multiplatformConfig`)

Modules applying `multiplatform`, `multiplatformCompose`, or `multiplatformMinimalist` have access to the `multiplatformConfig` DSL:

```kotlin
multiplatformConfig {
    useFirebase() // Injects Firebase KMP (Auth, Firestore, Storage) in commonMain + Firebase BoM/libs in androidMain
    useCoil()     // Injects coil-compose in commonMain (requires Compose plugin)
}
```

### Shared Configuration & Rules

- **Android SDK & Toolchain**:
  - `compileSdk = 36`, `minSdk = 26`, `targetSdk = 35`
  - Java Version: `JavaVersion.VERSION_21` via `jvmToolchain(21)`
- **Dynamic Namespaces**:
  - Pattern: `com.msoula.hobbymatchmaker.<module.path>` (e.g., `:features:movies:data` → `com.msoula.hobbymatchmaker.features.movies.data`).
- **iOS Targets**:
  - Standard targets: `iosArm64()` and `iosSimulatorArm64()`.
  - Can be conditionally disabled during fast Android builds via `-Pkmp.buildTargets.ios.enabled=false`.
- **Application Identifiers (`ProjectApplication`)**:
  - Application ID: `com.msoula.hobbymatchmaker`
  - Version: `1.0.0` (Code `1`).
- **Version Catalog**:
  - All dependency references must be retrieved dynamically via `libs` catalog (`libs.findLibrary(...)`). No hardcoded versions.

---

### Plugin Selection Decision Matrix

```text
Is it a Presentation / UI module?
 └── YES ➔ Apply `hobbymatchmaker.buildlogic.multiplatformCompose`
 └── NO:
      Is it a Data / Network / DB module?
       └── YES ➔ Apply `hobbymatchmaker.buildlogic.multiplatform`
       └── NO (Domain / Pure Logic / Contracts):
            └── Apply `hobbymatchmaker.buildlogic.multiplatformMinimalist`

Need testing utilities (Kotest, Turbine)?
 └── Add `hobbymatchmaker.buildlogic.multiplatformTest` alongside the base plugin.
```

---

## Key Libraries & Versions

| Concern | Library | Version |
|---|---|---|
| UI framework | Compose Multiplatform | `1.10.3` |
| UI components | Material3 (Compose) | `1.9.0` |
| Navigation | `org.jetbrains.androidx.navigation:navigation-compose` | `2.9.2` |
| DI | Koin | `4.2.1` |
| Networking | Ktor Client (CIO on Android, Darwin on iOS) | `3.4.3` |
| Local DB | SQLDelight | `2.3.2` |
| Remote DB | Firebase KMP (Auth, Firestore, Storage) | `2.4.0` |
| Firebase iOS SDK | firebase-ios-sdk (via SPM) | `12.10.0` |
| Preferences | DataStore Preferences | `1.2.1` |
| Serialization | KotlinX Serialization JSON | `1.11.0` |
| Image loading | Coil 3 (`coil-compose`, `coil-network-ktor3`) | `3.4.0` |
| Async | KotlinX Coroutines | `1.10.2` |
| Observability | Kotzilla SDK | `2.1.3` |
| Build secrets (KMP) | BuildKonfig | `0.20.0` |
| Testing | Kotest + Turbine | `6.1.11` / `1.2.1` |
| Kotlin | Kotlin Gradle Plugin | `2.3.21` |

---

## Naming Conventions

| Entity                    | Pattern | Example                   |
|---------------------------|---|---------------------------|
| ViewModel                 | `*ViewModel` | `MovieViewModel`          |
| UseCase                   | `*UseCase` | `ObserveAllMoviesUseCase` |
| Repository Interface      | `*Repository` | `MovieRepository`         |
| Repository Implementation | `*RepositoryImpl` | `MovieRepositoryImpl`     |
| Local Data Source         | `*DataSource` | `MovieLocalDataSource`    |
| Remote Data Source        | `*DataSource` | `MovieRemoteDataSource`   |
| Domain Model              | `*DomainModel` | `MovieDomainModel`        |
| UI Model                  | `*UiModel` | `MovieUiModel`            |
| Screen Composable         | `*Screen` or `*Content` | `MovieContent`            |
| DI Module                 | `*Module` | `MovieDomainModule`       |

- UseCase names: `GetX`, `ObserveX`, `SetX`, `UpdateX`, `DeleteX`
- Boolean use cases must start with `Is` or `Has`
- Avoid generic names like `handle`, `process`, `executeStuff`
- Module constants live in `build-logic:convention:Versions.kt` under the `Modules` object

---

## Checklist: Adding a New Feature Module

- [ ] Create `:features:<name>:domain`, `:features:<name>:data`, `:features:<name>:presentation` modules (omit layers with no content)
- [ ] Apply appropriate convention plugin:
  - `multiplatformCompose` for `:presentation` / `:design`
  - `multiplatform` for `:data`
  - `multiplatformMinimalist` for `:domain`
  - (Optional) `multiplatformTest` for testing
- [ ] Verify no cross-feature dependencies are introduced
- [ ] Add module constants to `build-logic:convention:Versions.kt` under `Modules`
- [ ] Add presentation module to `core/navigation/presentation/build.gradle.kts`
- [ ] Include the module(s) in `settings.gradle.kts`
- [ ] Wire module(s) into `:composeApp` `commonMain.dependencies`
