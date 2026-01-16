# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HobbyMatchMaker is a **Kotlin Multiplatform (KMP)** project targeting Android and iOS. It's a movie recommendation app with social features, built using Clean Architecture principles with MVVM pattern and Jetpack Compose Multiplatform for UI.

## Build Commands

### Gradle Tasks
```bash
# Clean build artifacts
./gradlew clean

# Build for all platforms
./gradlew build

# Android-specific
./gradlew :composeApp:assembleDebug          # Build debug APK
./gradlew :composeApp:installDebug           # Install on connected device
./gradlew :composeApp:assembleRelease        # Build release APK

# iOS-specific (requires macOS)
./gradlew compileKotlinIosSimulatorArm64     # Build iOS simulator
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode  # For Xcode integration

# Module-specific builds
./gradlew :core:database:build               # Build specific module
./gradlew :features:movies:presentation:build

# Lint and Code Quality
./gradlew detekt                             # Run static analysis
./gradlew koverHtmlReport                    # Generate code coverage report
```

### iOS Development
The iOS app is in `iosApp/` and uses Xcode. After Kotlin changes:
1. Run `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`
2. Open `iosApp/iosApp.xcodeproj` in Xcode
3. Build and run from Xcode

## Architecture

### Module Structure
The project follows **modular Clean Architecture** with three-layer pattern per feature:

```
core/              # Shared infrastructure (multiplatform)
├── authentication # Auth logic
├── common         # Utilities, error handling, extensions
├── database       # SQLDelight schemas & DAOs
├── design         # Design system, DI, UI components
├── login          # Login feature
├── navigation     # Compose Navigation setup
├── network        # Ktor HTTP client
├── session        # User session management
└── splashscreen   # Splash screen

features/          # Business features (domain/data/presentation)
├── movies         # Movie browsing & list
├── moviedetail    # Movie detail view
├── profile        # User profile management
└── social         # Social features (invitations, connections)
```

**Each feature module follows:**
- `domain/` - Pure Kotlin business logic (use cases, repository interfaces)
- `data/` - Repository implementations, data sources (local/remote), mappers
- `presentation/` - UI (Compose), ViewModels, Interactors, event handling

### State Management Pattern

The project uses a **sealed interface-based state system** with comprehensive documentation in:
- `README_STATES.md` - Complete state management reference
- `QUICK_START.md` - 3-step template for new screens
- `GUIDE_STATES_USAGE.md` - Detailed component usage

**Core State Types:**
```kotlin
// UI State (4 states: Loading, Success, Error, Empty)
sealed interface UiState<out T>

// Form State (6 states: Idle, Validation, Invalid, Submitting, Success, Error)
sealed interface FormState<out T>

// Result Wrapper (Success/Failure for operations)
sealed class AppResult<out Success, out Error>
```

**ViewModels provides:
- State management via `StateFlow<UiState<T>>`
- Event emission via `EventHandler` (Flow-based)
- Lifecycle-aware coroutine scope

**Events are handled through:**
```kotlin
sealed interface UiEvent {
    data class ShowSnackBar(val message: UIText)
    data class Navigate(val destination: NavigationDestination)
    data class OnDataReady(val data: String)
    // ... other events
}
```

### Navigation System

**Type-safe navigation** using Compose Navigation Multiplatform with kotlinx.serialization:

```kotlin
// Routes defined in core/navigation/presentation/Route.kt
@Serializable object Movies
@Serializable data class MovieDetail(val id: Long)
@Serializable object Profile
```

**Navigation is centralized in `AppNavHost`:**
- Use `NavigationCallbacks` for common flows (e.g., `navigateToMoviesFromAuth()`)
- **CRITICAL:** Always call navigation callbacks with `()` - they are functions, not properties
- ViewModels emit `UiEvent.Navigate`, composables collect and execute navigation
- Use `LaunchedEffect` blocks to observe ViewModel events and trigger navigation

### Dependency Injection (Koin)

**Module organization:**
- Each layer defines its own DI module in `di/*.kt`
- Core modules in `core/design/src/commonMain/kotlin/.../di/DiModule.kt`
- Feature modules: `*DomainModule`, `*DataModule`, `*ViewModelModule`

**Usage in Compose:**
```kotlin
val viewModel: MovieViewModel = koinViewModel()
val viewModelWithParams: MovieDetailViewModel = koinViewModel(
    key = "movieDetail-$movieId",
    parameters = { parametersOf(movieId) }
)
```

### Database (SQLDelight)

**Schema location:** `core/database/src/commonMain/sqldelight/hmm_database.sq`

**Tables:**
- `movie` - Movies with metadata (genres as JSON, video keys, local paths)
- `actor` + `movie_actor_cross_ref` - Cast relationships
- `user_profile` - User data (interests as JSON)
- `social_circle_member` - Social connections

**Key patterns:**
- DAOs return `Flow<List<T>>` for reactive queries
- Use `observeMovies()` not `getMovies()` for live data
- Mappers convert between Entity → Domain → UI models
- Repository layer orchestrates local + remote data sources

### Error Handling

**Hierarchical error types in `AppError`:**
```kotlin
sealed interface AppError {
    sealed interface Network    // Timeout, Unreachable, Http
    sealed interface Domain      // Unauthorized, Validation
    sealed interface Storage     // WriteFailed, ReadFailed
    sealed interface Authentication
    sealed interface External
}
```

**Error mapping:**
- `ErrorMessageMapper` converts `AppError` → `UIText`
- `UIText.Resource()` for i18n strings
- `UIText.Text()` for dynamic messages

## Naming Conventions

| Entity | Pattern | Example |
|--------|---------|---------|
| ViewModel | `*ViewModel` | `MovieViewModel` |
| UseCase | `*UseCase` | `ObserveAllMoviesUseCase` |
| Repository Interface | `*Repository` | `MovieRepository` |
| Repository Implementation | `*RepositoryImpl` | `MovieRepositoryImpl` |
| Data Source | `*DataSource` | `MovieLocalDataSource` |
| Domain Model | `*DomainModel` | `MovieDomainModel` |
| UI Model | `*UiModel` | `MovieUiModel` |
| Screen Composable | `*Screen` or `*Content` | `MovieContent` |
| DI Module | `*Module` | `MovieDomainModule` |

## Common Patterns

### Creating a New Feature
1. Create module structure: `domain/`, `data/`, `presentation/`
2. Define domain models and repository interface in `domain/`
3. Implement repository and data sources in `data/`
4. Create ViewModel in `presentation/`
5. Build UI composable observing ViewModel state
6. Add DI modules for each layer
7. Register route in `core/navigation/presentation/Route.kt`
8. Add composable to `AppNavHost`

### Flow-Based Observation Pattern
```kotlin
// In ViewModel init
init {
    observeData()
}

private fun observeData() {
    scope.launch {
        repository.observeData().collect { result ->
            when (result) {
                is AppResult.Success -> _state.update { UiState.Success(result.data) }
                is AppResult.Failure -> _state.update { UiState.Error(mapper.toUIText(result.error)) }
            }
        }
    }
}
```

### Launching Background Operations
```kotlin
// In ViewModel
scope.launch {
    when (val result = useCase()) {
        is AppResult.Success -> eventHandler.sendEvent(UiEvent.ShowSnackBar(...))
        is AppResult.Failure -> _state.update { UiState.Error(...) }
    }
}
```

## Platform-Specific Code

Use `expect`/`actual` declarations:

```kotlin
// commonMain
expect fun getDeviceLocale(): String

// androidMain
actual fun getDeviceLocale(): String = Locale.getDefault().language

// iosMain
actual fun getDeviceLocale(): String = NSLocale.currentLocale.languageCode
```

## Important Implementation Details

### Navigation Bug Prevention
**CRITICAL:** Navigation callbacks must be invoked with `()`:
```kotlin
// ✅ CORRECT
NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromAuth()

// ❌ WRONG (function reference, never executes)
NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromAuth
```

### ViewModel State Updates
Always use `.update { }` for thread-safe state mutations:
```kotlin
// ✅ CORRECT
_screenState.update { UiState.Success(data) }

// ❌ AVOID
_screenState.value = UiState.Success(data)
```

### Coroutine Scope Usage
- Use `viewModelScope` for ViewModel operations
- Use `scope.launch` (delegates to `viewModelScope`)
- Never create custom scopes in ViewModels, inject scope (easier to test)

### Flow Observation in Compose
```kotlin
@Composable
fun MovieScreen(viewModel: MovieViewModel = koinViewModel()) {
    val state by viewModel.screenState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> // Handle navigation
                is UiEvent.ShowSnackBar -> // Show snackbar
            }
        }
    }

    // UI based on state
}
```

### Image Loading
Uses Coil 3 with Ktor integration:
- Images cached in memory + disk
- Network images downloaded via `ImageRepository`
- Local paths stored in database for offline access

## Configuration

**Secrets Management:**
- Android: `secrets.properties` (not committed)
- iOS: `iosApp/iosApp/secrets.xcconfig` (not committed)
- Template files exist with `_TEMPLATE` suffix

**API Keys Required:**
- TMDB API key
- Firebase configuration (google-services.json, GoogleService-Info.plist)
- Facebook App ID (optional)

## Gradle Convention Plugins

Custom plugins in `build-logic/convention/` provide reusable configurations:
- `hobbymatchmaker.buildlogic.multiplatform` - Full KMP setup
- `hobbymatchmaker.buildlogic.multiplatform.compose` - Adds Compose UI
- `hobbymatchmaker.buildlogic.application` - App-level config

Apply in module `build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}
```

## Resources & Documentation

- `README_STATES.md` - **Start here** for state management
- `QUICK_START.md` - Template for new screens
- `GUIDE_STATES_USAGE.md` - Detailed component reference
- `EXEMPLES_CAS_USAGE.md` - Real-world examples
