# Guide d'utilisation : Gestion des états (Loading, Error, Empty)

Ce guide vous explique comment utiliser les nouveaux composants de gestion d'états dans votre architecture Clean Architecture + MVVM.

---

## 📋 Table des matières

1. [UiState - La sealed interface](#1-uistate)
2. [StateContainer - Le composant principal](#2-statecontainer)
3. [Composants de Loading](#3-composants-de-loading)
4. [ErrorStateScreen](#4-errorstatescreen)
5. [EmptyStateScreen](#5-emptystatescreen)
6. [UiEvent](#6-uievent)
7. [Exemples complets](#8-exemples-complets)

---

## 1. UiState

**Localisation** : `core/design/src/commonMain/kotlin/.../util/UiState.kt`

### Qu'est-ce que c'est ?

`UiState` est une sealed interface qui représente tous les états possibles de votre UI.

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: UIText, val hint: UIErrorHint = UIErrorHint()) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}
```

### Comment l'utiliser dans un ViewModel ?

**Exemple 1 : Liste de films**

```kotlin
class MovieViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    val moviesState: StateFlow<UiState<List<MovieUiModel>>>
        field = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Loading)

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            moviesState.value = UiState.Loading

            getMoviesUseCase()
                .onSuccess { movies ->
                    moviesState.value = if (movies.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(movies)
                    }
                }
                .onFailure { error ->
                    moviesState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }
}
```

**Exemple 2 : Détail d'un film**

```kotlin
class MovieDetailViewModel(
    private val movieId: Long,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    val movieDetailState: StateFlow<UiState<MovieDetailUiModel>>
        field = MutableStateFlow<UiState<MovieDetailUiModel>>(UiState.Loading)

    init {
        loadMovieDetail()
    }

    fun loadMovieDetail() {
        viewModelScope.launch {
            movieDetailState.value = UiState.Loading

            getMovieDetailUseCase(movieId)
                .onSuccess { detail ->
                    movieDetailState.value = UiState.Success(detail)
                }
                .onFailure { error ->
                    movieDetailState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }
}
```

---

## 2. StateContainer

**Localisation** : `core/design/src/commonMain/kotlin/.../atoms/Containers.kt`

### Qu'est-ce que c'est ?

`StateContainer` est un composant qui gère automatiquement l'affichage selon l'état (`UiState`).

### Comment l'utiliser ?

**Exemple 1 : Liste de films (simple)**

```kotlin
@Composable
fun MoviesScreen(
    viewModel: MovieViewModel = koinViewModel()
) {
    val moviesState by viewModel.moviesState.collectAsState()

    StateContainer(
        state = moviesState,
        onLoading = { LoadingCircularProgress() },
        onEmpty = {
            EmptyStateScreen(
                config = EmptyStateConfig(
                    icon = Icons.Outlined.MovieFilter,
                    title = UIText.Resource(Res.string.no_movies_title),
                    description = UIText.Resource(Res.string.no_movies_description),
                    ctaText = UIText.Resource(Res.string.refresh),
                    ctaAction = { viewModel.loadMovies() }
                )
            )
        },
        onError = { error, hint ->
            ErrorStateScreen(
                error = error,
                hint = hint,
                onRetry = { viewModel.loadMovies() }
            )
        },
        onSuccess = { movies ->
            MovieList(movies = movies)
        }
    )
}
```

**Exemple 2 : Détail de film avec loading personnalisé**

```kotlin
@Composable
fun MovieDetailScreen(
    movieId: Long,
    viewModel: MovieDetailViewModel = koinViewModel()
) {
    val detailState by viewModel.movieDetailState.collectAsState()

    StateContainer(
        state = detailState,
        onLoading = {
            // Loading personnalisé avec shimmer
            MovieDetailLoadingScreen()
        },
        onError = { error, hint ->
            ErrorStateScreen(
                error = error,
                hint = hint,
                onRetry = { viewModel.loadMovieDetail() }
            )
        },
        onSuccess = { movieDetail ->
            MovieDetailContent(movieDetail = movieDetail)
        }
    )
}
```

**Exemple 3 : Avec Scaffold et gestion manuelle**

```kotlin
@Composable
fun MoviesScreen(viewModel: MovieViewModel = koinViewModel()) {
    val moviesState by viewModel.moviesState.collectAsState()

    Scaffold(
        topBar = { MovieTopBar() }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = moviesState,
                onLoading = {
                    MovieListLoadingScreen(itemCount = 5)
                },
                onEmpty = {
                    EmptyStateScreen(
                        config = EmptyStateConfig(
                            icon = Icons.Outlined.MovieFilter,
                            title = UIText.Resource(Res.string.no_movies)
                        )
                    )
                },
                onError = { error, hint ->
                    ErrorStateScreen(error, hint, onRetry = { viewModel.loadMovies() })
                },
                onSuccess = { movies ->
                    LazyColumn {
                        items(movies) { movie ->
                            MovieCard(movie = movie)
                        }
                    }
                }
            )
        }
    }
}
```

---

## 3. Composants de Loading

**Localisation** : `core/design/src/commonMain/kotlin/.../atoms/Loaders.kt`

### 3.1 LoadingCircularProgress

Affiche un simple `CircularProgressIndicator` centré.

```kotlin
@Composable
fun MyScreen() {
    LoadingCircularProgress()
}
```

### 3.2 LoadingOverlay

Affiche un overlay semi-transparent avec un loader par-dessus le contenu.

```kotlin
@Composable
fun MyScreen(isLoading: Boolean) {
    Box {
        // Votre contenu
        MyContent()

        // Overlay de loading
        LoadingOverlay(visible = isLoading)
    }
}
```

**Exemple pratique : Formulaire en cours de soumission**

```kotlin
@Composable
fun SignUpForm(viewModel: SignUpViewModel) {
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    Box {
        Column {
            TextField(...)
            Button(onClick = { viewModel.submit() }) {
                Text("S'inscrire")
            }
        }

        // Overlay pendant la soumission
        LoadingOverlay(visible = isSubmitting)
    }
}
```

### 3.3 MovieListLoadingScreen

Affiche des cartes shimmer pour une liste de films.

```kotlin
@Composable
fun MoviesScreen(viewModel: MovieViewModel) {
    val state by viewModel.moviesState.collectAsState()

    StateContainer(
        state = state,
        onLoading = {
            MovieListLoadingScreen(itemCount = 5) // 5 cartes shimmer
        },
        onSuccess = { movies ->
            // Afficher les films réels
        }
    )
}
```

### 3.4 MovieDetailLoadingScreen

Affiche un shimmer pour le détail d'un film.

```kotlin
@Composable
fun MovieDetailScreen(viewModel: MovieDetailViewModel) {
    val state by viewModel.movieDetailState.collectAsState()

    StateContainer(
        state = state,
        onLoading = {
            MovieDetailLoadingScreen()
        },
        onSuccess = { detail ->
            // Afficher le détail réel
        }
    )
}
```

---

## 4. ErrorStateScreen

**Localisation** : `core/design/src/commonMain/kotlin/.../atoms/Containers.kt`

### Qu'est-ce que c'est ?

Affiche un écran d'erreur avec icône, message, et bouton retry optionnel.

### Paramètres

- `error: UIText` - Le message d'erreur
- `hint: UIErrorHint` - Indications sur l'erreur (retry policy, etc.)
- `onRetry: (() -> Unit)?` - Action à effectuer lors du retry

### Exemples

**Exemple 1 : Erreur simple avec retry**

```kotlin
@Composable
fun MoviesScreen(viewModel: MovieViewModel) {
    val state by viewModel.moviesState.collectAsState()

    StateContainer(
        state = state,
        onError = { error, hint ->
            ErrorStateScreen(
                error = error,
                hint = hint,
                onRetry = { viewModel.loadMovies() }
            )
        },
        onSuccess = { /* ... */ }
    )
}
```

**Exemple 2 : Erreur sans retry**

```kotlin
ErrorStateScreen(
    error = UIText.Resource(Res.string.permission_denied),
    hint = UIErrorHint(retry = RetryPolicy.Never),
    onRetry = null // Pas de bouton retry
)
```

**Exemple 3 : Erreur personnalisée**

```kotlin
when (state) {
    is UiState.Error -> {
        ErrorStateScreen(
            error = state.error,
            hint = state.hint,
            onRetry = if (state.hint.retry != RetryPolicy.Never) {
                { viewModel.retry() }
            } else null
        )
    }
    // autres cas...
}
```

---

## 5. EmptyStateScreen

**Localisation** : `core/design/src/commonMain/kotlin/.../atoms/Containers.kt`

### Qu'est-ce que c'est ?

Affiche un écran vide avec icône, titre, description optionnelle, et CTA optionnel.

### Configuration

```kotlin
data class EmptyStateConfig(
    val icon: ImageVector,           // Icône à afficher
    val title: UIText,                // Titre principal
    val description: UIText? = null,  // Description optionnelle
    val ctaText: UIText? = null,      // Texte du bouton
    val ctaAction: (() -> Unit)? = null // Action du bouton
)
```

### Exemples

**Exemple 1 : Liste de films vide**

```kotlin
@Composable
fun MoviesScreen(viewModel: MovieViewModel) {
    val state by viewModel.moviesState.collectAsState()

    StateContainer(
        state = state,
        onEmpty = {
            EmptyStateScreen(
                config = EmptyStateConfig(
                    icon = Icons.Outlined.MovieFilter,
                    title = UIText.Resource(Res.string.no_movies_title),
                    description = UIText.Resource(Res.string.no_movies_description),
                    ctaText = UIText.Resource(Res.string.browse_movies),
                    ctaAction = { /* Navigation vers catalogue */ }
                )
            )
        },
        onSuccess = { /* ... */ }
    )
}
```

**Exemple 2 : Favoris vides**

```kotlin
EmptyStateScreen(
    config = EmptyStateConfig(
        icon = Icons.Outlined.FavoriteBorder,
        title = UIText.Resource(Res.string.no_favorites_title),
        description = UIText.Resource(Res.string.no_favorites_description)
        // Pas de CTA
    )
)
```

**Exemple 3 : Recherche sans résultats**

```kotlin
EmptyStateScreen(
    config = EmptyStateConfig(
        icon = Icons.Outlined.SearchOff,
        title = UIText.Resource(Res.string.no_results),
        description = UIText.Plain("Aucun résultat pour \"${searchQuery}\""),
        ctaText = UIText.Resource(Res.string.clear_search),
        ctaAction = { viewModel.clearSearch() }
    )
)
```
---

## 6. UiEvent

**Localisation** : `core/design/src/commonMain/kotlin/.../util/UiEvent.kt`

### Qu'est-ce que c'est ?

Des événements one-time (navigation, snackbar, dialog) séparés de l'état.

```kotlin
sealed interface UiEvent {
    data class ShowSnackBar(val message: UIText) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data class OpenDialog(val title: UIText, val message: UIText) : UiEvent
}
```

### Comment l'utiliser ?

**Dans le Composable :**

```kotlin
@Composable
fun MoviesScreen(
    viewModel: MovieViewModel = koinViewModel(),
    onNavigate: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Collecter les événements
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message.asString())
                }
                is UiEvent.Navigate -> {
                    onNavigate(event.route)
                }
                is UiEvent.OpenDialog -> {
                    // Afficher un dialog
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        // Votre contenu
    }
}
```

---

## 7. Exemples complets

### Exemple complet 1 : Écran de liste de films

**ViewModel :**

```kotlin
class MovieViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    val movieState: StateFlow<UiState<*>>
        field = MutableStateFlow<UiState<*>>(UiState.Loading)

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            movieState.value = UiState.Loading

            getMoviesUseCase()
                .onSuccess { movies ->
                    movieState.value = if (movies.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(movies)
                    }
                }
                .onFailure { error ->
                    movieState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }

    fun onMovieClick(movieId: Long) {
        viewModelScope.launch {
            sendEvent(UiEvent.Navigate("/movie/$movieId"))
        }
    }
}
```

**Screen :**

```kotlin
@Composable
fun MoviesScreen(
    viewModel: MovieViewModel = koinViewModel(),
    onNavigate: (String) -> Unit
) {
    val moviesState by viewModel.moviesState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message.asString())
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Films") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = moviesState,
                onLoading = {
                    MovieListLoadingScreen(itemCount = 5)
                },
                onEmpty = {
                    EmptyStateScreen(
                        config = EmptyStateConfig(
                            icon = Icons.Outlined.MovieFilter,
                            title = UIText.Resource(Res.string.no_movies),
                            description = UIText.Resource(Res.string.no_movies_description),
                            ctaText = UIText.Resource(Res.string.retry),
                            ctaAction = { viewModel.loadMovies() }
                        )
                    )
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = { viewModel.loadMovies() }
                    )
                },
                onSuccess = { movies ->
                    LazyColumn {
                        items(movies) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { viewModel.onMovieClick(movie.id) }
                            )
                        }
                    }
                }
            )
        }
    }
}
```

### Exemple complet 2 : Détail de film

**ViewModel :**

```kotlin
class MovieDetailViewModel(
    private val movieId: Long,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    val detailState: StateFlow<UiState<*>>
        field = MutableStateFlow<UiState<MovieDetailUiModel>>(UiState.Loading)

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            detailState.value = UiState.Loading

            getMovieDetailUseCase(movieId)
                .onSuccess { detail ->
                    detailState.value = UiState.Success(detail)
                }
                .onFailure { error ->
                    detailState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }
}
```

**Screen :**

```kotlin
@Composable
fun MovieDetailScreen(
    movieId: Long,
    viewModel: MovieDetailViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Arrow_back, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = detailState,
                onLoading = {
                    MovieDetailLoadingScreen()
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = { viewModel.loadDetail() }
                    )
                },
                onSuccess = { detail ->
                    MovieDetailContent(movieDetail = detail)
                }
            )
        }
    }
}
```

---

## ✅ Checklist d'utilisation

Pour chaque nouvel écran :

1. ✅ Créer un ViewModel qui expose un `StateFlow<UiState<T>>`
2. ✅ Utiliser `StateContainer` dans le Composable
3. ✅ Définir les comportements pour : `onLoading`, `onEmpty`, `onError`, `onSuccess`
4. ✅ Utiliser les composants de loading appropriés (shimmer pour les listes)
5. ✅ Configurer `EmptyStateScreen` avec icône et message adaptés
6. ✅ Ajouter un `onRetry` pour `ErrorStateScreen` si pertinent
7. ✅ Utiliser `UiEvent` pour les événements one-time (navigation, snackbar)

---

## 🎯 Bonnes pratiques

1. **Toujours gérer les 4 états** : Loading, Success, Error, Empty
2. **Utiliser des shimmer** pour le loading de listes (meilleure UX)
3. **Personnaliser les Empty states** selon le contexte (favoris, recherche, etc.)
4. **Mapper les erreurs** avec `ErrorMessageMapper` pour des messages cohérents
5. **Séparer état et événements** : State pour données, Events pour actions one-time
6. **Tester tous les états** : Assurez-vous que chaque état s'affiche correctement
