---
name: kmp-ui-pattern
description: |
  Hybrid MVI + MVVM presentation layer for HobbyMatchmaker KMP — multiple StateFlow per ViewModel, *EventModel sealed interfaces, onEvent() dispatch, Orchestrators as domain facades, externalScope testability, flat stable UI models, and *Content/*Screen composable naming. Use this skill whenever creating or reviewing a ViewModel, defining screen state, event models, UI models, composable structure, or Orchestrators. Trigger on phrases like "add a ViewModel", "create a screen", "MVI", "state", "EventModel", "onEvent", "screen composable", "UiModel", "Orchestrator", or "externalScope".
---

# Presentation Layer Architecture — HobbyMatchmaker KMP

## Pattern: Hybrid MVI + MVVM

This project combines MVI-style event dispatch with MVVM-style multiple state flows. It does **not** use a single `State` data class or `Action`/`Intent` naming. Key characteristics:

| Aspect | This project |
|---|---|
| Input model | Sealed interface `*EventModel`, dispatched via `onEvent()` |
| State model | Multiple `StateFlow` per ViewModel (one per concern) |
| Side effects | `EventHandler` (Channel-based) emitting `UiEvent` |
| Coroutine scope | `externalScope: CoroutineScope? = null` → `private val scope = externalScope ?: viewModelScope` |
| Domain facade | `*Orchestrator` aggregates multiple use cases |
| UI models | `data class *UiModel` (flat, plain `List`) with `companion object { val Initial }` |
| Composables | `*Content` or `*Screen` (no `*Root`/`*Screen` split) wired inside `AppNavHost` |

---

## EventModel (MVI-style input)

Every ViewModel defines a sealed interface named `*EventModel`. All user actions are routed through a single `onEvent()` method.

```kotlin
sealed interface MovieEventModel {
    data class OnCardDoubleTap(val movie: MovieUiModel) : MovieEventModel
    data class OnCardSingleTap(val movieId: Long, val movieOverview: String?) : MovieEventModel
    data object RetryMovies : MovieEventModel
    data object OnRefresh : MovieEventModel
}

sealed interface UserProfileUiEventModel {
    data class OnAvatarSelected(val bytes: ByteArray) : UserProfileUiEventModel
    data class OnPseudoChanged(val pseudo: String) : UserProfileUiEventModel
    data object OnSaveProfile : UserProfileUiEventModel
    data object OnSignOut : UserProfileUiEventModel
    data object OnEditProfile : UserProfileUiEventModel
}
```

**Rules:**
- Suffix: always `*EventModel` (not `*Action`, `*Intent`).
- Data carriers use `data class`; parameter-less events use `data object`.
- Keep event names user-action-oriented (`OnCardDoubleTap`, `OnSaveProfile`), not implementation-oriented.

---

## ViewModel Structure

### Multiple StateFlow (not a single state object)

Each ViewModel exposes **one `StateFlow` per concern**, not a single aggregated state class.

```kotlin
class MovieViewModel(
    private val movieCatalogOrchestrator: MovieCatalogOrchestrator,
    private val userActionOrchestrator: MovieUserActionOrchestrator,
    private val defaultMessageMapper: DefaultErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    // Separate flows per concern
    private val _movieScreenState = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Loading)
    val movieScreenState: StateFlow<UiState<List<MovieUiModel>>> = _movieScreenState.asStateFlow()

    private val _paginationState = MutableStateFlow(PaginationStateModel())
    val paginationState: StateFlow<PaginationStateModel> = _paginationState.asStateFlow()

    val events = movieCatalogOrchestrator.eventHandler.events
```

```kotlin
class UserProfileViewModel(
    private val userProfileInteractor: UserProfileInteractor,
    private val defaultMessageMapper: DefaultErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    val profileState: StateFlow<UiState<UserProfileUiModel>> = ...
    val formState: StateFlow<FormState<UserProfileFormModel>> = ...
    val events = eventHandler.events
```

Always update with `.update { }`:
```kotlin
_movieScreenState.update { UiState.Loading }
_paginationState.update { it.copy(isLoadingMore = true) }
```

### onEvent() dispatch

```kotlin
fun onEvent(event: MovieEventModel) {
    when (event) {
        is MovieEventModel.OnCardDoubleTap -> scope.launch { toggleFavorite(event.movie.id) }
        is MovieEventModel.OnCardSingleTap -> scope.launch {
            handleSingleTap(event.movieId, event.movieOverview)
        }
        is MovieEventModel.RetryMovies -> observeMovies()
        is MovieEventModel.OnRefresh -> refreshMovies()
    }
}
```

Simple, non-async operations (e.g., UI-only mode toggles) may also be exposed as direct methods alongside `onEvent()`:
```kotlin
fun loadMore() { scope.launch { ... } }
fun closeEdition() { _formState.update { FormState.Idle(currentValue) } }
```

### externalScope — testability

Every ViewModel accepts `externalScope: CoroutineScope? = null`. At runtime Koin passes `null` → `viewModelScope` is used. In tests, pass a `TestScope` or `UnconfinedTestDispatcher` scope:

```kotlin
class MovieViewModel(
    private val catalogOrchestrator: MovieCatalogOrchestrator,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope
    ...
}
```

```kotlin
// In Koin module
viewModel {
    MovieViewModel(
        movieCatalogOrchestrator = get(),
        userActionOrchestrator = get(),
        defaultMessageMapper = get(),
        externalScope = null   // null = use viewModelScope at runtime
    )
}
```

---

## Orchestrators — Domain Facades

Orchestrators aggregate multiple use cases into a single entry point for a ViewModel. They live in `:features:<name>:presentation` (not domain) because they may hold `EventHandler` or coordinate UI-level concerns.

```kotlin
class MovieCatalogOrchestrator(
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val refreshMoviesUseCase: RefreshMoviesUseCase,
    private val observeLikedMoviesIdsUseCase: ObserveLikedMoviesIdsUseCase,
    val eventHandler: EventHandler     // shared event bus
) {
    fun observeMovies(): Flow<List<MovieDomainModel>> = observeAllMoviesUseCase()
    suspend fun refresh(language: String): AppResult<Unit, AppError> = refreshMoviesUseCase(language)
}

class MovieUserActionOrchestrator(
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val syncFavoriteToCircleUseCase: SyncFavoriteToCircleUseCase
) {
    suspend fun toggleFavorite(movieId: Long, isFavorite: Boolean): AppResult<Unit, AppError> = ...
}
```

**Rules:**
- Orchestrators are `factoryOf(::...)` in Koin (not `singleOf`).
- ViewModel receives the Orchestrator, not individual use cases.
- `EventHandler` is typically owned by one Orchestrator and exposed via `val eventHandler`.

---

## UI Models (`*UiModel`)

UI models are presentation-layer data classes. They hold pre-formatted strings (dates, units) ready for display.

```kotlin
data class MovieUiModel(
    val id: Long = 0L,
    val title: String = "",
    val coverFileName: String = "",
    val localCoverFilePath: String = "",
    val releaseDate: String = "",
    val isFavorite: Boolean = false,
    val overview: String = "",
    val note: Double = 0.0
) {
    companion object {
        val Initial = MovieUiModel()
    }
}

data class UserProfileUiModel(
    val uid: String = "",
    val displayName: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = null,
    val interests: List<String> = emptyList()
) {
    companion object {
        val Initial = UserProfileUiModel()
    }
}
```

**Rules:**
- Don't annotate with `@Immutable`. UI models live in presentation modules (Compose compiler applied), so flat `val` data classes are inferred stable, and Strong Skipping skips unchanged `List` instances by `===`. See `/kmp-compose` → "Collections & stability".
- Suffix: `*UiModel` (not `*Ui`, `*ViewState`).
- All fields have defaults.
- Always include `companion object { val Initial = ... }`.
- Use plain `List<T>` for list fields, not `ImmutableList<T>`.
- Never nullable collections: default to `emptyList()`.
- Existing UI models (movies, profile) still use `@Immutable` + `ImmutableList`. Leave them as they are unless they feed a large lazy list.

### Mappers

Mapper extension functions live alongside the UI model or in a `mappers/` subfolder:

```kotlin
fun MovieDomainModel.toMovieUiModel(): MovieUiModel = MovieUiModel(
    id = id,
    title = title,
    coverFileName = coverFileName,
    localCoverFilePath = localCoverFilePath,
    releaseDate = releaseDate,
    isFavorite = isFavorite,
    overview = overview,
    note = note
)
```

---

## Side Effects — EventHandler + UiEvent

ViewModels emit one-time side effects via `EventHandler`, never via `StateFlow`.

```kotlin
// EventHandler is injected or owned by an Orchestrator
class EventHandler {
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    suspend fun sendEvent(event: UiEvent) { _events.send(event) }
}

// In ViewModel
private fun handleSingleTap(movieId: Long) {
    scope.launch {
        eventHandler.sendEvent(
            UiEvent.Navigate(NavigationDestination.MovieDetail(movieId))
        )
    }
}

private fun handleError(error: AppError) {
    scope.launch {
        eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))
    }
}
```

Refer to `kmp-compose` skill for `UiEvent` variants and `LaunchedEffect(Unit)` collection pattern.

---

## Composable Structure

### No Root/Screen split

This project does **not** use a `*Root` / `*Screen` composable pair. Composables are named `*Content` or `*Screen` and wired directly inside `AppNavHost` `composable<>` blocks.

```kotlin
// In AppNavHost composable<Movies> block
composable<Movies> {
    val vm = koinViewModel<MovieViewModel>()

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> when (event.destination) {
                    is NavigationDestination.MovieDetail ->
                        navCallbacks.navigateToMovieDetailFromMoviesOrHub(event.destination.movieId)
                    else -> Unit
                }
                is UiEvent.ShowSnackBar -> snackbarHostState.showSnackbar(
                    event.message.asStringSuspend()
                )
                else -> Unit
            }
        }
    }

    MovieContent(
        movieScreenState = vm.movieScreenState.collectAsState().value,
        paginationState = vm.paginationState.collectAsState().value,
        onEvent = vm::onEvent,
        onNavigate = { destination -> ... }
    )
}
```

### Screen composable

```kotlin
@Composable
fun MovieContent(
    movieScreenState: UiState<List<MovieUiModel>>,
    paginationState: PaginationStateModel,
    onEvent: (MovieEventModel) -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pure rendering — no ViewModel reference
    StateContainer(
        state = movieScreenState,
        onLoading = { MovieListLoadingScreen() },
        onEmpty = { EmptyStateScreen(emptyConfig) },
        onError = { error, hint -> ErrorStateScreen(error, hint) },
        onSuccess = { movies ->
            MovieCarousel(
                movies = movies,
                onCardDoubleTap = { movie -> onEvent(MovieEventModel.OnCardDoubleTap(movie)) },
                onCardSingleTap = { id, overview -> onEvent(MovieEventModel.OnCardSingleTap(id, overview)) }
            )
        }
    )
}
```

### State collection

Always use `collectAsState()` (not `collectAsStateWithLifecycle()`):
```kotlin
val movieScreenState by vm.movieScreenState.collectAsState()
val paginationState by vm.paginationState.collectAsState()
```

---

## UiState and FormState

Async data states use `UiState<T>`, form lifecycle uses `FormState<T>`. Both defined in `../../../core/design`.

```kotlin
// In ViewModel — set state
_profileState.update { UiState.Loading }
_profileState.update { UiState.Success(domainModel.toUiModel()) }
_profileState.update { UiState.Error(defaultMessageMapper.toUIText(error), error.hint()) }
_profileState.update { UiState.Empty }

// Form states
_formState.update { FormState.Idle(currentValue) }
_formState.update { FormState.Submitting(currentValue) }
_formState.update { FormState.Invalid(currentValue, mapOf("pseudo" to "Already taken")) }
_formState.update { FormState.Success(result) }
```

---

## ViewModel init pattern

Reactive data observation starts in `init {}`:

```kotlin
init {
    observeMovies()
}

private fun observeMovies() {
    scope.launch {
        movieCatalogOrchestrator.observeMovies()
            .map { movies -> movies.map { it.toMovieUiModel() } }
            .collect { uiModels ->
                _movieScreenState.update {
                    if (uiModels.isEmpty()) UiState.Empty
                    else UiState.Success(uiModels)
                }
            }
    }
}
```

For one-shot operations triggered by user action:
```kotlin
private fun refreshMovies() {
    scope.launch {
        _movieScreenState.update { UiState.Loading }
        when (val result = movieCatalogOrchestrator.refresh(getDeviceLocale())) {
            is AppResult.Success -> Unit  // observe flow handles state update
            is AppResult.Failure -> handleError(result.error)
        }
    }
}
```

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| ViewModel | `*ViewModel` | `MovieViewModel`, `UserProfileViewModel` |
| Event model | `*EventModel` | `MovieEventModel`, `UserProfileUiEventModel` |
| UI model | `*UiModel` | `MovieUiModel`, `UserProfileUiModel` |
| Orchestrator | `*Orchestrator` | `MovieCatalogOrchestrator`, `UserProfileOrchestrator` |
| Screen composable | `*Content` or `*Screen` | `MovieContent`, `UserProfileScreen` |
| State flow (async data) | `*State: StateFlow<UiState<T>>` | `movieScreenState`, `profileState` |
| State flow (form) | `*State: StateFlow<FormState<T>>` | `formState` |
| State flow (pagination/misc) | descriptive name | `paginationState`, `socialCircleState` |
| Entry method | `onEvent(event: *EventModel)` | `onEvent(event: MovieEventModel)` |
| Direct method (simple) | verb + noun | `loadMore()`, `closeEdition()` |

---

## Checklist: Adding a New Screen

- [ ] Define `*EventModel` sealed interface in `features:<name>:presentation`
- [ ] Implement `*ViewModel` with multiple `StateFlow` fields (one per concern)
- [ ] Use `externalScope: CoroutineScope? = null` → `private val scope = externalScope ?: viewModelScope`
- [ ] Define `*Orchestrator`(s) if multiple use cases need coordination
- [ ] Start reactive observation in `init { observeX() }`
- [ ] Expose `val events = orchestrator.eventHandler.events` (or own `EventHandler`)
- [ ] Create `*UiModel` (flat `data class`, plain `List`, no `@Immutable`), with defaults and `companion object { val Initial }`
- [ ] Write `fun DomainModel.toUiModel()` mapper extension in presentation layer
- [ ] Create `*Content` or `*Screen` composable (state + onEvent, no ViewModel reference)
- [ ] Wire ViewModel in `AppNavHost` `composable<>` block with `koinViewModel()`
- [ ] Add `LaunchedEffect(Unit)` in `AppNavHost` block to collect events and call `navCallbacks`
- [ ] Register Orchestrator as `factoryOf(::...)` and ViewModel as `viewModel { ... }` in Koin module
- [ ] Add Koin module to `appModule()` in `KoinInit.kt`
