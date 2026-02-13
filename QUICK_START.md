# Quick Start : Gestion des états

Guide rapide pour démarrer avec le nouveau système de gestion d'états.

---

## 🚀 En 3 étapes simples

### 1️⃣ Dans le ViewModel

```kotlin
class MyViewModel(
    private val useCase: MyUseCase,
    private val errorMapper: ErrorMessageMapper
) {

    // 1. Créer un StateFlow avec UiState<T> -- Using Kotlin 2.3.0
    val myState: StateFlow<UiState<MyData>>
        field = MutableStateFlow<UiState<MyData>>(UiState.Loading)

    override val screenState: StateFlow<UiState<*>> = myState

    init {
        loadData()
    }

    // 2. Charger les données
    fun loadData() {
        viewModelScope.launch {
            myState.value = UiState.Loading

            useCase()
                .onSuccess { data ->
                    myState.value = if (data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(data)
                    }
                }
                .onFailure { error ->
                    myState.value = UiState.Error(
                        error = errorMapper.toUIText(error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
        }
    }

    // 3. Envoyer des événements
    fun onItemClick(id: Long) {
        viewModelScope.launch {
            sendEvent(UiEvent.Navigate("/detail/$id"))
        }
    }
}
```

### 2️⃣ Dans le Screen

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = koinViewModel(),
    onNavigate: (String) -> Unit
) {
    val myState by viewModel.myState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Collecter les événements
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            // 2. Utiliser StateContainer
            StateContainer(
                state = myState,
                onLoading = { LoadingCircularProgress() },
                onEmpty = {
                    EmptyStateScreen(
                        config = EmptyStateConfig(
                            icon = Icons.Outlined.Info,
                            title = UIText.Resource(Res.string.no_data)
                        )
                    )
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = { viewModel.loadData() }
                    )
                },
                onSuccess = { data ->
                    // Afficher vos données
                    MyContent(data = data)
                }
            )
        }
    }
}
```

### 3️⃣ Ajouter les ressources strings

```xml
<!-- Dans core/design/src/commonMain/composeResources/values/strings.xml -->
<string name="no_data">Aucune donnée disponible</string>
<string name="error_issue_retry">Réessayer</string>
```

---

## 📦 Composants disponibles

### Loading

```kotlin
// Simple loader centré
LoadingCircularProgress()

// Overlay semi-transparent
LoadingOverlay(visible = isLoading)

// Liste avec shimmer (5 cartes)
MovieListLoadingScreen(itemCount = 5)

// Détail avec shimmer
MovieDetailLoadingScreen()
```

### Error

```kotlin
ErrorStateScreen(
    error = UIText.Resource(Res.string.error_message),
    hint = UIErrorHint(retry = RetryPolicy.Manual),
    onRetry = { viewModel.retry() }
)
```

### Empty

```kotlin
EmptyStateScreen(
    config = EmptyStateConfig(
        icon = Icons.Outlined.MovieFilter,
        title = UIText.Resource(Res.string.no_movies),
        description = UIText.Resource(Res.string.no_movies_description),
        ctaText = UIText.Resource(Res.string.browse_movies),
        ctaAction = { /* Action */ }
    )
)
```

---

## 🎯 Types UiState

```kotlin
UiState.Loading           // En cours de chargement
UiState.Success(data)     // Données chargées avec succès
UiState.Error(error, hint) // Erreur survenue
UiState.Empty             // Pas de données (liste vide, etc.)
```

---

## 📤 Types UiEvent

```kotlin
UiEvent.ShowSnackBar(message)      // Afficher un snackbar
UiEvent.Navigate(route)            // Naviguer vers une route
UiEvent.OpenDialog(title, message) // Ouvrir un dialog
```

---

## ✅ Checklist

Pour chaque nouvel écran :

- [ ] Créer `StateFlow<UiState<T>>`
- [ ] Implémenter `override val screenState`
- [ ] Gérer les 4 états : Loading, Success, Error, Empty
- [ ] Utiliser `StateContainer` dans le Composable
- [ ] Collecter les `events` avec `LaunchedEffect`
- [ ] Ajouter les ressources strings nécessaires

---

## 📚 Documentation complète

- **GUIDE_STATES_USAGE.md** : Guide détaillé de chaque composant
- **MIGRATION_EXAMPLE.md** : Migration du code existant
- **EXEMPLES_CAS_USAGE.md** : Cas d'usage spécifiques (pagination, search, etc.)

---

## 🆘 Aide rapide

**Je veux afficher un loader pendant le chargement :**
```kotlin
onLoading = { LoadingCircularProgress() }
```

**Je veux afficher un message quand la liste est vide :**
```kotlin
onEmpty = {
    EmptyStateScreen(
        config = EmptyStateConfig(
            icon = Icons.Outlined.Info,
            title = UIText.Resource(Res.string.no_data)
        )
    )
}
```

**Je veux afficher une erreur avec bouton retry :**
```kotlin
onError = { error, hint ->
    ErrorStateScreen(
        error = error,
        hint = hint,
        onRetry = { viewModel.retry() }
    )
}
```

**Je veux naviguer vers un autre écran :**
```kotlin
// Dans le ViewModel
viewModelScope.launch {
    sendEvent(UiEvent.Navigate("/detail/$id"))
}

// Dans le Screen
LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
        when (event) {
            is UiEvent.Navigate -> onNavigate(event.route)
            else -> {}
        }
    }
}
```

**Je veux afficher un snackbar :**
```kotlin
// Dans le ViewModel
viewModelScope.launch {
    sendEvent(UiEvent.ShowSnackBar(UIText.Resource(Res.string.success)))
}
```

**Je veux un overlay de loading (formulaire en cours de soumission) :**
```kotlin
Box {
    MyForm()
    LoadingOverlay(visible = isSubmitting)
}
```
