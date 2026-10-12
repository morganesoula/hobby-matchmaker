---
name: kmp-navigation
description: |
  Type-safe Compose Navigation for this KMP project — route objects, NavigationDestination events, NavigationCallbacks, AppNavHost structure, two-level nav (root + tab), and iOS wiring. Use this skill whenever setting up navigation, adding a route, navigating between screens, emitting a navigation event from a ViewModel, or wiring the iOS entry point. Trigger on phrases like "add a route", "navigate to", "navigation event", "NavController", "NavigationDestination", "NavigationCallbacks", "AppNavHost", "tab navigation", or "iOS entry point".
---

# Navigation Architecture — HobbyMatchmaker KMP

## Key Insight: Two Separate Concepts

Navigation uses **two distinct types** that must not be confused:

| Type | Location | Purpose |
|---|---|---|
| `Route` objects (`Splash`, `Movies`, …) | `core/navigation/presentation` — `Route.kt` | Compose Navigation destinations — passed to `NavController` |
| `NavigationDestination` sealed class | `core/design` — `UiEvent.kt` | ViewModel-level intent — carried inside `UiEvent.Navigate` |

ViewModels never touch `Route` objects. `AppNavHost` translates `NavigationDestination` → `NavController` call via `NavigationCallbacks`.

---

## Route Objects (`Route.kt`)

All routes live in a single file in `:core:navigation:presentation`. Every object has an explicit `@SerialName`.

```kotlin
// core/navigation/presentation/…/Route.kt
@Serializable @SerialName("splash")      object Splash
@Serializable @SerialName("auth")        object Auth
@Serializable @SerialName("sign_in")     object SignIn
@Serializable @SerialName("sign_up")     object SignUp
@Serializable @SerialName("main")        object Main       // tab scaffold entry point
@Serializable @SerialName("hub")         object Hub
@Serializable @SerialName("movies")      object Movies
@Serializable @SerialName("profile")     object Profile
@Serializable @SerialName("social")      object Social
@Serializable @SerialName("movie_detail") data class MovieDetail(val id: Long)
```

**Rules:**
- Use `object` for parameter-less destinations, `data class` for destinations with arguments.
- No `Route` suffix — routes are named after the screen directly.
- Always add `@SerialName` to avoid issues with minification or obfuscation.
- Add every new route here; never define routes inside a feature module.

---

## NavigationDestination (ViewModel Side)

`NavigationDestination` is a sealed class in `core/design`, carried inside `UiEvent.Navigate`. ViewModels emit it; they never reference `Route` objects.

```kotlin
// core/design/…/util/UiEvent.kt
sealed interface UiEvent {
    data class Navigate(val destination: NavigationDestination) : UiEvent
    data class ShowSnackBar(val message: UIText) : UiEvent
    data class OnDataReady(val data: String) : UiEvent
    data class OpenDialog(val dialogPurpose: String) : UiEvent
    data class CloseDialog(val dialogName: String) : UiEvent
    data class ShowAnimation(val data: MatchAnimationData) : UiEvent
    data object CapacityReached : UiEvent
}

sealed class NavigationDestination {
    data class MovieDetail(val movieId: Long) : NavigationDestination()
    data object Movies    : NavigationDestination()
    data object Profile   : NavigationDestination()
    data object SignIn    : NavigationDestination()
    data object SignUp    : NavigationDestination()
    data object Social    : NavigationDestination()
}
```

**Emitting from a ViewModel:**

```kotlin
// In a ViewModel — never import Route objects here
private fun logOut() {
    scope.launch {
        logOutUseCase().onSuccess {
            eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.SignIn))
        }
    }
}

private suspend fun handleMovieTap(movieId: Long) {
    eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.MovieDetail(movieId)))
}
```

---

## NavigationCallbacks

`NavigationCallbacks` is an `@Immutable` class that wraps `NavController` and exposes **named, typed navigation actions**. It is created once in `AppNavHost` via `remember`.

```kotlin
// core/navigation/presentation/…/utils/NavigationCallbacks.kt
@Immutable
class NavigationCallbacks(private val navController: NavController) {

    // Simple lambdas stored as val — MUST be invoked with ()
    val navigateAndClearToAuth: () -> Unit = {
        navController.navigate(Auth) { popUpTo(0) { inclusive = true }; launchSingleTop = true }
    }
    val navigateToAuthFromSplash: () -> Unit = {
        navController.navigate(Auth) { popUpTo<Splash> { inclusive = true }; launchSingleTop = true }
    }
    val navigateToMoviesFromSplash: () -> Unit = {
        navController.navigate(Main) { popUpTo<Splash> { inclusive = true }; launchSingleTop = true }
    }
    val navigateToMoviesFromAuth: () -> Unit = {
        navController.navigate(Main) { popUpTo<Auth> { inclusive = true }; launchSingleTop = true }
    }
    val navigateToProfileFromMovies: () -> Unit = {
        navController.navigate(Profile) { launchSingleTop = true }
    }
    val navigateToSocialFromMovies: () -> Unit = {
        navController.navigate(Social) { launchSingleTop = true }
    }

    // fun for parameterized navigation
    fun navigateToMovieDetailFromMoviesOrHub(movieId: Long) =
        navController.navigate(MovieDetail(movieId)) { launchSingleTop = true }
}
```

**CRITICAL — Always invoke callbacks with `()`:**

```kotlin
// ✅ CORRECT
NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromAuth()

// ❌ WRONG — passes the function reference, never executes
NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromAuth
```

When adding a new navigation path, add a new named `val` (or `fun` if parameterized) to `NavigationCallbacks`. Never call `navController.navigate(...)` inline from a `LaunchedEffect`.

---

## Two-Level Navigation Structure

The app uses **two nested NavHosts**:

```
AppNavHost (root NavHost — startDestination = Splash)
├── Splash
├── Auth  (nested navigation graph)
│   ├── SignIn  (startDestination)
│   └── SignUp
├── Main  ← entry into MainScaffold
│   └── MainScaffold (inner NavHost with bottom bar — startDestination = Movies)
│       ├── Movies   (tab)
│       └── Hub      (tab)
├── MovieDetail  (overlaid on top of Main, back → returns to tab)
├── Profile      (overlaid on top of Main)
└── Social       (overlaid on top of Main)
```

`MainScaffold` has its **own** `rememberNavController()` (the tab controller). Tab switching uses `launchSingleTop = true`, `restoreState = true`, `saveState = true`.

---

## AppNavHost Structure

`AppNavHost` is the only place that:
1. Creates `rememberNavController()` and `NavigationCallbacks`
2. Instantiates ViewModels via `koinViewModel()`
3. Collects `UiEvent` flows in `LaunchedEffect(Unit)` and translates them to `navCallbacks` calls
4. Passes `onNavigate: (NavigationDestination) -> Unit` lambdas into screen composables

```kotlin
@Composable
fun AppNavHost(socialClients: SocialClients) {
    val nav = rememberNavController()
    val navCallbacks = remember(nav) { NavigationCallbacks(nav) }

    // SocialViewModel is shared across Profile, Social, and Movies
    val socialViewModel = koinViewModel<SocialViewModel>()

    NavHost(navController = nav, startDestination = Splash) {

        composable<Splash> {
            val vm = koinViewModel<SplashViewModel>()
            SplashScreenContent(
                state = vm.state.collectAsState().value,
                redirectToAuth = navCallbacks.navigateToAuthFromSplash,       // () not needed — it's passed as lambda
                redirectToMovies = navCallbacks.navigateToMoviesFromSplash
            )
        }

        navigation<Auth>(startDestination = SignIn) {
            composable<SignIn> {
                val vm = koinViewModel<SignInViewModel> { parametersOf(SocialClientsVM(socialClients.clients)) }
                LaunchedEffect(Unit) {
                    vm.events.collect { event ->
                        when (event) {
                            is UiEvent.Navigate -> when (event.destination) {
                                NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromAuth()
                                NavigationDestination.SignUp -> nav.navigate(SignUp)
                                else -> Unit
                            }
                            // …
                        }
                    }
                }
                SignInScreenContent(/* … */)
            }
            composable<SignUp> { /* … */ }
        }

        composable<Main> { MainScaffold(navCallbacks) }

        composable<MovieDetail> { backStackEntry ->
            val movieId = backStackEntry.toRoute<MovieDetail>().id
            val vm = koinViewModel<MovieDetailViewModel>(
                key = "movieDetail-$movieId",
                parameters = { parametersOf(movieId) }
            )
            // Collect events, render MovieDetailContent
        }

        composable<Profile> { /* uses shared socialViewModel */ }
        composable<Social>  { /* uses shared socialViewModel */ }
    }
}
```

**Key patterns:**
- One `LaunchedEffect(Unit)` per ViewModel per `composable<>` block — never outside.
- The `key` parameter on `koinViewModel` is required for parameterized ViewModels to avoid instance reuse across different back-stack entries.
- `SocialViewModel` is instantiated once at the `AppNavHost` level and passed down to multiple screens.

---

## Shared ViewModels Across Destinations

`SocialViewModel` is created once at the root `AppNavHost` level so its state is shared between `Profile`, `Social`, and `Movies` without re-creation:

```kotlin
// In AppNavHost — created once
val socialViewModel = koinViewModel<SocialViewModel>()

// Then passed into Profile composable block, Social composable block, and Movies
```

Follow this pattern only when two screens genuinely share live state. Don't hoist ViewModels up unnecessarily.

---

## Screen Composables — Navigation Lambdas

Screen composables accept navigation as a lambda parameter; they never hold or import a `NavController`. Two patterns are used:

**Pattern A — single `onNavigate` lambda:**
```kotlin
@Composable
fun MovieContent(
    // …
    onNavigate: (NavigationDestination) -> Unit,
    // …
) {
    NavigationTopBar(
        redirectToProfile = { onNavigate(NavigationDestination.Profile) },
        redirectToSocial  = { onNavigate(NavigationDestination.Social) }
    )
}
```

**Pattern B — actions object (for complex screens):**
```kotlin
// Bundled into a remember-stable data class
val userProfileActions = remember(userProfileViewModel, socialViewModel) {
    UserProfileActions(
        onNavigate = { destination ->
            when (destination) {
                NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromProfile()
                else -> Unit
            }
        },
        // … other callbacks
    )
}
UserProfileContent(userProfileActions = userProfileActions, /* … */)
```

Use Pattern B when a screen composable would need more than ~3 navigation-related lambdas.

---

## iOS Entry Point

```
iOSApp.swift (SwiftUI @main)
  └── WindowGroup { ComposeView() }
        ↑ renders Kotlin/Compose UI

AppDelegate.swift
  └── application(_:didFinishLaunchingWithOptions:)
        ├── FirebaseApp.configure()
        └── KoinHelperKt.doInitKoin()   ← starts Koin with all modules

KoinHelper.kt (iosMain)
  └── initKoin() — registers PlatformContext.INSTANCE + appModule()

MainViewController.kt (iosMain)
  └── ComposeUIViewController {
        val socialClients = SocialClients(
            mapOf(GOOGLE to GoogleUIClientImpl(IosGoogleUIClient()),
                  APPLE  to AppleUIClientImpl(IosAppleUIClient()))
        )
        App(socialClients = socialClients)
      }
```

`SocialClients` is constructed in `MainViewController` on iOS with Google + Apple providers (Android uses Google + Facebook instead).

---

## Tab Navigation (MainTab / Bottom Bar)

Bottom-bar tabs are defined by `MainTab` enum in `Route.kt`:

```kotlin
enum class MainTab(val route: Any, val label: String, val icon: ImageVector) {
    MOVIES(Movies, "Movies", LucideFilm),
    HUB(Hub, "Hub", VscodeCodiconsLightbulb)
}
```

Tab switching always uses these options to preserve back-stack state:
```kotlin
tabNavController.navigate(tab.route) {
    launchSingleTop = true
    restoreState = true
    popUpTo(tabNavController.graph.startDestinationId) { saveState = true }
}
```

To add a new tab: add an entry to `MainTab`, add the route object to `Route.kt`, add a `composable<NewTab>` block inside `MainScaffold`'s inner `NavHost`.

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Route object | Screen name, no suffix | `Splash`, `MovieDetail`, `Profile` |
| Route `@SerialName` | snake_case | `"movie_detail"`, `"sign_in"` |
| NavigationDestination | Screen name, no suffix | `NavigationDestination.MovieDetail(id)` |
| NavigationCallbacks method | `navigateTo<Destination>From<Origin>` | `navigateToMoviesFromAuth` |
| NavCallbacks clear method | `navigateAndClearTo<Destination>` | `navigateAndClearToAuth` |

---

## Checklist: Adding Navigation to a New Screen

- [ ] Add a `@Serializable @SerialName("…") object/data class` to `Route.kt`
- [ ] Add a `data object` (or `data class` if parameterized) to `NavigationDestination` in `UiEvent.kt` — only if a ViewModel needs to trigger this navigation
- [ ] Add a named `val` (or `fun`) to `NavigationCallbacks` describing the path
- [ ] Add a `composable<NewRoute>` block in `AppNavHost` (or inside `MainScaffold` if it's a tab)
- [ ] Instantiate the ViewModel with `koinViewModel()` inside the `composable` block; use `key` if parameterized
- [ ] Add a `LaunchedEffect(Unit)` to collect `vm.events` and call `navCallbacks.*()` — always invoke with `()`
- [ ] Pass `onNavigate` lambda into the screen composable; never pass `NavController` directly