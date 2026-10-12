---
name: kmp-di
description: |
  Koin dependency injection setup for this Android/KMP project — module definitions per layer, ViewModel injection, platform-specific bindings, and assembling modules in KoinInit. Use this skill whenever setting up Koin, defining a DI module, providing a repository or ViewModel, injecting a dependency, or wiring modules in the Application class. Trigger on phrases like "set up Koin", "add a Koin module", "inject a dependency", "DI module", "koinViewModel", "provide a ViewModel", "startKoin", "single/viewModel/factory", or "platform-specific binding".
---

# Dependency Injection (Koin) — HobbyMatchmaker KMP

## Principles

- One Koin module per feature layer — only create it if there are dependencies to provide.
- All modules are aggregated in `composeApp/…/navigation/KoinInit.kt` via `appModule()`. Feature modules never call `startKoin`.
- Use `koinViewModel()` in composables, never instantiate ViewModels manually.
- Domain use cases → `factoryOf` (stateless, new instance per injection).
- Repositories / data sources → `singleOf` (one instance for the app lifetime).
- ViewModels → `viewModel { }` (scoped to their Compose lifecycle).

---

## Module Aggregation (`KoinInit.kt`)

All modules are collected into a single `List<Module>` in `composeApp`:

```kotlin
// composeApp/…/navigation/KoinInit.kt
fun appModule() = listOf(
    coreModuleNetwork,
    coreModuleSessionData,
    coreModuleSessionDomain,
    coreModuleAuthenticationData,
    coreModuleAuthenticationDomain,
    coreModuleCommon,
    coreModuleDAO,
    coreModuleDi,
    coreModuleLogin,
    coreModuleLoginFormValidation,
    coreModuleSignInViewModel,
    coreModuleSignUpViewModel,
    coreModuleUserData,
    featuresModuleMovieData,
    featuresModuleMovieDomain,
    featuresModuleMovieViewModel,
    featuresModuleMovieDetailData,
    featuresModuleMovieDetailDomain,
    featuresModuleMovieDetailViewModel,
    featuresModuleSocialData,
    featuresModuleSocialDomain,
    featuresModuleSocialPresentation,
    featuresModuleUserProfileData,
    featuresModuleUserProfileDomain,
    featuresModuleUserProfilePresentation,
    coreModuleSplashPresentation,
    featuresModuleHubDomain,
    featuresModuleHubPresentation,
)
```

When you add a new feature module, add its module val(s) here — and nowhere else.

---

## Koin Startup

### Android (`HobbyMatchMakerApplication.kt`)

```kotlin
class HobbyMatchMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HobbyMatchMakerApplication)
            modules(appModule() + coreCommonAndroidSavedStateHandle)
        }
    }
}
```

`coreCommonAndroidSavedStateHandle` (Android-only) provides `SavedStateHandle` as a singleton.

### iOS (`KoinHelper.kt`)

```kotlin
fun initKoin() {
    startKoin {
        modules(
            module {
                single<PlatformContext> { PlatformContext.INSTANCE }  // required by Coil
            },
            *appModule().toTypedArray()
        )
        monitoring()  // Kotzilla
    }
}
```

The `PlatformContext.INSTANCE` binding is iOS-specific — Android resolves it from `androidContext()`.

---

## Module Definitions by Layer

### Domain layer → `factoryOf` only

Use cases are stateless: create a fresh instance on every injection.

```kotlin
// features/movies/domain/…/di/MovieDomainModule.kt
val featuresModuleMovieDomain = module {
    factoryOf(::ObserveAllMoviesUseCase)
    factoryOf(::RefreshMoviesUseCase)
    factoryOf(::SetMovieFavoriteUseCase)
    // ...
}
```

Exception: when a use case requires manual wiring (e.g. composing sub-use-cases inline), use the lambda form:

```kotlin
val coreModuleLoginFormValidation = module {
    factory {
        LoginValidateFormUseCase(
            ValidateEmailUseCase(),
            ValidatePasswordUseCase(),
            ValidateNameUseCase()
        )
    }
}
```

### Data layer → `singleOf` + `bind`

Repositories and data sources live for the entire app lifetime.

```kotlin
// features/social/data/…/di/SocialDataModule.kt
val featuresModuleSocialData = module {
    singleOf(::SocialLocalDataSourceImpl) bind SocialLocalDataSource::class
    singleOf(::SocialRemoteDataSourceImpl) bind SocialRemoteDataSource::class
    singleOf(::SocialRepositoryImpl) bind SocialRepository::class
}
```

Mark critical dependencies `createdAtStart = true` when they must be ready before the first screen:

```kotlin
single<MovieLocalDataSource>(createdAtStart = true) { MovieLocalDataSourceImpl(get()) }
```

### Presentation layer → `viewModel` + `factoryOf` for orchestrators

```kotlin
// features/profile/presentation/…/di/UserProfilePresentationModule.kt
val featuresModuleUserProfilePresentation = module {
    factoryOf(::UserProfileOrchestrator)

    viewModel {
        UserProfileViewModel(
            userProfileInteractor = get(),
            defaultMessageMapper = get(),
            checkPseudoUseCase = get(),
            logOutUseCase = get(),
            upsertUserUseCase = get(),
            syncUserUseCase = get(),
            refreshSocialCircleUseCase = get(),
            observeSessionStateUseCase = get(),
            externalScope = null           // null = use viewModelScope at runtime
        )
    }
}
```

### ViewModels with runtime parameters

When a ViewModel requires a value only known at navigation time (e.g. a route argument), use the lambda form with destructuring:

```kotlin
// features/moviedetail/presentation/…/di/MovieDetailViewModelModule.kt (android + ios)
viewModel { (movieId: Long) ->
    MovieDetailViewModel(
        movieId = movieId,
        interactor = get(),
        syncFavoriteToCircleUseCase = get(),
        checkMovieMatchUseCase = get(),
        defaultMessageMapper = get()
    )
}
```

```kotlin
// features/login/presentation/…/di/SignInViewModelModule.kt
viewModel { (socialClients: SocialClientsVM) ->
    SignInViewModel(
        signInInteractor = get(),
        socialClients = socialClients,
        defaultErrorMessageMapper = get()
    )
}
```

---

## Platform-Specific Modules (`expect` / `actual`)

Use `expect val`/`actual val` when a binding differs between Android and iOS, then compose with `includes()` in the common module.

```kotlin
// core/network/…/di/NetworkModule.kt  (commonMain)
val coreModuleNetwork = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    single<FirebaseAuth> { Firebase.auth }
    includes(coreModuleNetworkPlatformSpecific)   // ← composed here
    single<Json> { Json { ignoreUnknownKeys = true; ... } }
}
expect val coreModuleNetworkPlatformSpecific: Module
```

```kotlin
// androidMain — CIO engine + Android connectivity
actual val coreModuleNetworkPlatformSpecific = module {
    single<NetworkConnectivityChecker> { AndroidNetworkConnectivityChecker(get()) }
    single<HttpClient>(createdAtStart = true) { HttpClient(CIO) { ... } }
}

// iosMain — Darwin engine + iOS connectivity
actual val coreModuleNetworkPlatformSpecific = module {
    single<NetworkConnectivityChecker> { IOSNetworkConnectivityChecker() }
    single<HttpClient>(createdAtStart = true) { HttpClient(Darwin) { ... } }
}
```

Other platform-split patterns in this project:

| Concern | Android | iOS |
|---|---|---|
| Network engine | `HttpClient(CIO)` | `HttpClient(Darwin)` |
| DB driver | `AndroidDriverFactory(androidContext())` | `IosDriverFactory()` |
| Auth providers | `GoogleAuthProviderImpl` + `FacebookAuthProviderImpl` | `GoogleAuthProviderImpl` + `AppleAuthProviderImpl` |
| DataStore | `createDataStore(androidContext())` | `createDataStore()` |
| Context (Coil) | resolved from `androidContext()` | `PlatformContext.INSTANCE` (injected at startup) |

---

## Named Qualifiers

Use `named("…")` only when multiple bindings of the same type must coexist (rare):

```kotlin
// iOS only: separate dispatcher for image I/O
actual val featuresModuleMovieDataPlatformSpecific = module {
    single<CoroutineDispatcher>(named("imageDispatcher")) { Dispatchers.Default }
    single<ImageRepository> { ImageRepositoryImpl(get(named("imageDispatcher"))) }
}
```

---

## Injecting in Composables (`AppNavHost`)

```kotlin
// Parameterless
val movieViewModel: MovieViewModel = koinViewModel()

// With a single parameter
val signInViewModel = koinViewModel<SignInViewModel> {
    parametersOf(SocialClientsVM(socialClients.clients))
}

// With a key (prevents instance reuse across nav destinations) + parameter
val movieDetailViewModel = koinViewModel<MovieDetailViewModel>(
    key = "movieDetail-$movieId",
    parameters = { parametersOf(movieId) }
)
```

Use `key` whenever the same ViewModel type can be instantiated with different arguments in the same back-stack.

---

## Scoping Reference

| Scope | Form (preferred) | Fallback | When to use |
|---|---|---|---|
| Singleton | `singleOf(::Impl) { bind<Iface>() }` | `single { ... }` | Repositories, data sources, HttpClient, DB |
| Singleton (eager) | `single(createdAtStart = true) { ... }` | — | Critical deps that must be ready immediately |
| ViewModel | `viewModel { ... }` | `viewModelOf(::VM)` | Any ViewModel |
| Factory | `factoryOf(::Impl)` | `factory { ... }` | Use cases, orchestrators, validators |

Use `*Of` constructor-reference forms when all constructor parameters are already in the Koin graph. Use the lambda form only when you need to call a factory method, pass a named qualifier, or do post-construction setup.

---

## Naming Conventions

| Thing | Pattern | Examples |
|---|---|---|
| Core module val | `coreModule<Concern><Layer>` | `coreModuleNetwork`, `coreModuleSessionDomain`, `coreModuleAuthenticationData` |
| Feature module val | `featuresModule<Feature><Layer>` | `featuresModuleMovieDomain`, `featuresModuleSocialPresentation` |
| Platform-specific val | `<parentModuleName>PlatformSpecific` | `coreModuleNetworkPlatformSpecific`, `featuresModuleMovieDataPlatformSpecific` |
| DI file location | `di/` subdirectory within each layer | `features/movies/domain/…/di/MovieDomainModule.kt` |

---

## Checklist: Adding DI for a New Feature

- [ ] Create `val featuresModule{Name}Domain = module { factoryOf(::…) }` in `:features:{name}:domain/…/di/`
- [ ] Create `val featuresModule{Name}Data = module { singleOf(::…) bind … }` in `:features:{name}:data/…/di/`
- [ ] Create `val featuresModule{Name}Presentation = module { viewModel { … } }` in `:features:{name}:presentation/…/di/`
- [ ] If platform-specific bindings are needed, add `expect`/`actual` vals and `includes()` in the common module
- [ ] Register all three module vals in `appModule()` in `KoinInit.kt`
- [ ] Inject the ViewModel in composables via `koinViewModel()` (with `parametersOf` / `key` as needed)