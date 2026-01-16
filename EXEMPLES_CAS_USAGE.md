# Exemples de cas d'usage spécifiques

Ce document présente des exemples concrets pour différents scénarios d'utilisation.

---

## 🎬 Cas 1 : Liste de favoris (peut être vide)

### ViewModel

```kotlin
class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Loading)
    val favoritesState: StateFlow<UiState<List<MovieUiModel>>> = _favoritesState.asStateFlow()

    override val screenState: StateFlow<UiState<*>> = favoritesState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favoritesState.value = UiState.Loading

            getFavoritesUseCase()
                .onSuccess { favorites ->
                    _favoritesState.value = if (favorites.isEmpty()) {
                        UiState.Empty // 👈 Important : gérer le cas vide
                    } else {
                        UiState.Success(favorites)
                    }
                }
                .onFailure { error ->
                    _favoritesState.value = UiState.Error(
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

### Screen

```kotlin
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    onNavigate: (String) -> Unit
) {
    val favoritesState by viewModel.favoritesState.collectAsState()
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
            TopAppBar(title = { Text("Mes favoris") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = favoritesState,
                onLoading = {
                    MovieListLoadingScreen(itemCount = 3)
                },
                onEmpty = {
                    EmptyStateScreen(
                        config = EmptyStateConfig(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = UIText.Resource(Res.string.no_favorites_title),
                            description = UIText.Resource(Res.string.no_favorites_description),
                            ctaText = UIText.Resource(Res.string.browse_movies),
                            ctaAction = { onNavigate("/movies") }
                        )
                    )
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = { viewModel.loadFavorites() }
                    )
                },
                onSuccess = { favorites ->
                    LazyColumn {
                        items(favorites) { movie ->
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

---

## 🔍 Cas 2 : Recherche de films

### ViewModel

```kotlin
class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Empty)
    val searchState: StateFlow<UiState<List<MovieUiModel>>> = _searchState.asStateFlow()

    override val screenState: StateFlow<UiState<*>> = searchState

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        if (query.isBlank()) {
            _searchState.value = UiState.Empty
            return
        }

        if (query.length < 3) {
            // Attendre au moins 3 caractères
            return
        }

        search(query)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _searchState.value = UiState.Loading

            delay(300) // Debounce

            // Vérifier que la query n'a pas changé
            if (query != _searchQuery.value) return@launch

            searchMoviesUseCase(query)
                .onSuccess { results ->
                    _searchState.value = if (results.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(results)
                    }
                }
                .onFailure { error ->
                    _searchState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }

    fun retry() {
        if (_searchQuery.value.isNotBlank()) {
            search(_searchQuery.value)
        }
    }
}
```

### Screen

```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onNavigate: (String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchState by viewModel.searchState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Rechercher un film...") },
                        singleLine = true
                    )
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = searchState,
                onLoading = {
                    MovieListLoadingScreen(itemCount = 3)
                },
                onEmpty = {
                    EmptyStateScreen(
                        config = EmptyStateConfig(
                            icon = if (searchQuery.isBlank()) {
                                Icons.Outlined.Search
                            } else {
                                Icons.Outlined.SearchOff
                            },
                            title = if (searchQuery.isBlank()) {
                                UIText.Resource(Res.string.search_empty_title)
                            } else {
                                UIText.Resource(Res.string.no_results)
                            },
                            description = if (searchQuery.isBlank()) {
                                UIText.Resource(Res.string.search_empty_description)
                            } else {
                                UIText.Plain("Aucun résultat pour \"$searchQuery\"")
                            }
                        )
                    )
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = { viewModel.retry() }
                    )
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

## 👤 Cas 3 : Profil utilisateur avec formulaire

### ViewModel

```kotlin
class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfile>> = _profileState.asStateFlow()

    override val screenState: StateFlow<UiState<*>> = profileState

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading

            getUserProfileUseCase()
                .onSuccess { profile ->
                    _profileState.value = UiState.Success(profile)
                }
                .onFailure { error ->
                    _profileState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            _isUpdating.value = true

            updateProfileUseCase(name, email)
                .onSuccess { updatedProfile ->
                    _profileState.value = UiState.Success(updatedProfile)
                    sendEvent(
                        UiEvent.ShowSnackBar(
                            UIText.Resource(Res.string.profile_updated)
                        )
                    )
                }
                .onFailure { error ->
                    sendEvent(
                        UiEvent.ShowSnackBar(errorMapper.toUIText(error))
                    )
                }
                .also {
                    _isUpdating.value = false
                }
        }
    }
}
```

### Screen

```kotlin
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message.asString())
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mon profil") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = profileState,
                onLoading = {
                    LoadingCircularProgress()
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = { viewModel.loadProfile() }
                    )
                },
                onSuccess = { profile ->
                    // Initialiser les champs avec les données du profil
                    LaunchedEffect(profile) {
                        name = profile.name
                        email = profile.email
                    }

                    Box {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            TextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nom") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            SpacerHeight16()

                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            SpacerHeight24()

                            Button(
                                onClick = { viewModel.updateProfile(name, email) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating
                            ) {
                                Text("Enregistrer")
                            }
                        }

                        // Overlay de loading pendant la mise à jour
                        LoadingOverlay(visible = isUpdating)
                    }
                }
            )
        }
    }
}
```

---

## 📡 Cas 4 : Pagination (Load More)

### ViewModel

```kotlin
class MoviesWithPaginationViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    private val _moviesState = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Loading)
    val moviesState: StateFlow<UiState<List<MovieUiModel>>> = _moviesState.asStateFlow()

    override val screenState: StateFlow<UiState<*>> = moviesState

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 1
    private var canLoadMore = true

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _moviesState.value = UiState.Loading
            currentPage = 1

            getMoviesUseCase(page = currentPage)
                .onSuccess { movies ->
                    _moviesState.value = if (movies.isEmpty()) {
                        UiState.Empty
                    } else {
                        canLoadMore = movies.size >= 20 // Taille de page
                        UiState.Success(movies)
                    }
                }
                .onFailure { error ->
                    _moviesState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }

    fun loadMore() {
        if (!canLoadMore || _isLoadingMore.value) return

        val currentMovies = (_moviesState.value as? UiState.Success)?.data ?: return

        viewModelScope.launch {
            _isLoadingMore.value = true
            currentPage++

            getMoviesUseCase(page = currentPage)
                .onSuccess { newMovies ->
                    canLoadMore = newMovies.size >= 20
                    _moviesState.value = UiState.Success(currentMovies + newMovies)
                }
                .onFailure { error ->
                    currentPage-- // Revenir en arrière en cas d'erreur
                    sendEvent(
                        UiEvent.ShowSnackBar(errorMapper.toUIText(error))
                    )
                }
                .also {
                    _isLoadingMore.value = false
                }
        }
    }
}
```

### Screen

```kotlin
@Composable
fun MoviesWithPaginationScreen(
    viewModel: MoviesWithPaginationViewModel = koinViewModel()
) {
    val moviesState by viewModel.moviesState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // Détection du scroll en bas de la liste
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore) {
            viewModel.loadMore()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message.asString())
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Films") }) },
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
                            title = UIText.Resource(Res.string.no_movies)
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
                    LazyColumn(state = listState) {
                        items(movies) { movie ->
                            MovieCard(movie = movie)
                        }

                        // Indicateur de loading en bas
                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
```

---

## 🔄 Cas 5 : Pull-to-Refresh

### ViewModel

```kotlin
class MoviesWithRefreshViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val errorMapper: ErrorMessageMapper
) : ViewModel() {

    private val _moviesState = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Loading)
    val moviesState: StateFlow<UiState<List<MovieUiModel>>> = _moviesState.asStateFlow()

    override val screenState: StateFlow<UiState<*>> = moviesState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _moviesState.value = UiState.Loading

            getMoviesUseCase()
                .onSuccess { movies ->
                    _moviesState.value = if (movies.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(movies)
                    }
                }
                .onFailure { error ->
                    _moviesState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            getMoviesUseCase()
                .onSuccess { movies ->
                    _moviesState.value = if (movies.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(movies)
                    }
                }
                .onFailure { error ->
                    // En cas d'erreur lors du refresh, on garde les données actuelles
                    // et on affiche juste un snackbar
                    sendEvent(
                        UiEvent.ShowSnackBar(errorMapper.toUIText(error))
                    )
                }
                .also {
                    _isRefreshing.value = false
                }
        }
    }
}
```

### Screen

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesWithRefreshScreen(
    viewModel: MoviesWithRefreshViewModel = koinViewModel()
) {
    val moviesState by viewModel.moviesState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message.asString())
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Films") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
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
                            MovieCard(movie = movie)
                        }
                    }
                }
            )

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
```

---

## 🎯 Résumé des patterns

| Cas d'usage | État initial | Gestion Empty | Gestion Error | Actions |
|-------------|--------------|---------------|---------------|---------|
| **Liste simple** | `Loading` | ✅ Oui | ✅ Retry | Load |
| **Favoris** | `Loading` | ✅ Oui (CTA vers catalogue) | ✅ Retry | Load, Navigate |
| **Recherche** | `Empty` | ✅ Oui (varie selon query) | ✅ Retry | Search, Debounce |
| **Profil** | `Loading` | ❌ Non | ✅ Retry | Load, Update (overlay) |
| **Pagination** | `Loading` | ✅ Oui | ✅ Retry | Load, LoadMore |
| **Pull-to-Refresh** | `Loading` | ✅ Oui | ✅ Retry + Snackbar | Load, Refresh |
