# Système de gestion des états - Documentation

> Architecture Clean Architecture + MVVM avec Compose Multiplatform

---

## 📖 Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Composants](#composants)
4. [Documentation](#documentation)
5. [Migration](#migration)

---

## Vue d'ensemble

Ce système fournit une gestion d'états **unifiée, réutilisable et moderne** pour tous vos écrans.

### ✨ Avantages

- ✅ **Cohérence** : Même logique sur tous les écrans
- ✅ **Réutilisable** : Composants génériques
- ✅ **Moderne** : UX améliorée (shimmer, states, etc.)
- ✅ **Type-safe** : Sealed interfaces
- ✅ **Clean Architecture** : Séparation états/événements
- ✅ **Testable** : ViewModels faciles à tester

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         PRESENTATION                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐          ┌─────────────────────────┐  │
│  │     MyViewModel  │          │    StateContainer       │  │
│  │                  │          │                         │  │
│  │  StateFlow<      │◄─────────┤  • onLoading            │  │
│  │ UiState<T>>      │          │  • onSuccess            │  │
│  └──────────────────┘          │  • onError              │  │
│                                │  • onEmpty              │  │
│                                └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                          UI STATES                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  UiState.Loading ───────► LoadingCircularProgress()         │
│                      ───► MovieListLoadingScreen()          │
│                      ───► MovieDetailLoadingScreen()        │
│                                                             │
│  UiState.Success<T> ────► Your content (data: T)            │
│                                                             │
│  UiState.Error ──────────► ErrorStateScreen()               │
│                                                             │
│  UiState.Empty ──────────► EmptyStateScreen()               │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                         UI EVENTS                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  UiEvent.ShowSnackBar ───► SnackbarHost.showSnackbar()      │
│                                                             │
│  UiEvent.Navigate ───────► Navigator.navigate()             │
│                                                             │
│  UiEvent.OpenDialog ─────► Show AlertDialog                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Composants

### 🔷 Core (sealed interfaces)

| Composant | Localisation | Description |
|-----------|--------------|-------------|
| `UiState<T>` | `core/design/util/UiState.kt` | Sealed interface pour les états |
| `UiEvent` | `core/design/util/UiEvent.kt` | Sealed interface pour les événements |
| `UIText` | `core/design/util/ErrorMessageMapper.kt` | Textes typesafe |

### 🎨 Atoms (composables de base)

| Composant | Fichier | Usage |
|-----------|---------|-------|
| `StateContainer` | `atoms/Containers.kt` | Container principal pour gérer les états |
| `LoadingCircularProgress` | `atoms/Loaders.kt` | Loader simple centré |
| `LoadingOverlay` | `atoms/Loaders.kt` | Overlay semi-transparent avec loader |
| `MovieListLoadingScreen` | `atoms/Loaders.kt` | Liste avec shimmer |
| `MovieDetailLoadingScreen` | `atoms/Loaders.kt` | Détail avec shimmer |
| `ErrorStateScreen` | `atoms/Containers.kt` | Écran d'erreur avec retry |
| `EmptyStateScreen` | `atoms/Containers.kt` | Écran vide configurable |
| `ShimmerCard` | `atoms/Cards.kt` | Carte avec effet shimmer |
| `ShimmerRectangle` | `atoms/Cards.kt` | Rectangle avec effet shimmer |

---

## Documentation

### 📚 Guides disponibles

| Fichier | Description | Pour qui ? |
|---------|-------------|-----------|
| **QUICK_START.md** | Démarrage rapide en 3 étapes | 🚀 Débutants |
| **GUIDE_STATES_USAGE.md** | Guide détaillé de chaque composant | 📖 Référence complète |
| **MIGRATION_EXAMPLE.md** | Migration du code existant | 🔄 Migration |
| **EXEMPLES_CAS_USAGE.md** | Cas d'usage spécifiques | 💡 Cas avancés |
| **README_STATES.md** | Ce fichier - Vue d'ensemble | 🗺️ Vue globale |

### 🎯 Choisir le bon guide

**Je débute** → `QUICK_START.md`

**Je veux voir comment utiliser un composant** → `GUIDE_STATES_USAGE.md`

**Je veux migrer mon code existant** → `MIGRATION_EXAMPLE.md`

**Je cherche un cas spécifique (pagination, search...)** → `EXEMPLES_CAS_USAGE.md`

---

## Migration

### Étapes de migration

1. ✅ Lire `MIGRATION_EXAMPLE.md`
2. ✅ Remplacer les custom `StateModel` par `UiState<T>`
4. ✅ Utiliser `StateContainer` dans les Screens
5. ✅ Remplacer les custom events par `UiEvent`
6. ✅ Ajouter les ressources strings manquantes

### Exemple rapide : AVANT / APRÈS

**AVANT :**
```kotlin
// ViewModel
sealed interface MyState {
    object Loading : MyState
    data class Success(val data: List<Item>) : MyState
    data class Error(val message: String) : MyState
}

// Screen
when (state) {
    is MyState.Loading -> LoadingIndicator()
    is MyState.Success -> MyContent(state.data)
    is MyState.Error -> Text(state.message)
}
```

**APRÈS :**
```kotlin
// ViewModel
private val _state = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
val state: StateFlow<UiState<List<Item>>> = _state.asStateFlow()

// Screen
StateContainer(
    state = state,
    onLoading = { LoadingCircularProgress() },
    onEmpty = { EmptyStateScreen(...) },
    onError = { error, hint -> ErrorStateScreen(...) },
    onSuccess = { data -> MyContent(data) }
)
```

---

## 🎨 Preview des composants

### Loading States

```
┌─────────────────────────────┐
│                             │
│                             │
│         ⭕ Loading...       │
│                             │
│                             │
└─────────────────────────────┘
   LoadingCircularProgress

┌─────────────────────────────┐
│  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒  │
│  ▒▒▒▒▒▒▒▒▒▒▒  ▒▒▒▒▒▒▒▒▒▒  │
│  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒  │
│  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒  │
│  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒  │
└─────────────────────────────┘
   MovieListLoadingScreen
```

### Error State

```
┌─────────────────────────────┐
│                             │
│          ⚠️  Erreur         │
│                             │
│   Une erreur est survenue   │
│                             │
│      [  Réessayer  ]        │
│                             │
└─────────────────────────────┘
   ErrorStateScreen
```

### Empty State

```
┌─────────────────────────────┐
│                             │
│          🎬 (icon)          │
│                             │
│      Aucun film trouvé      │
│                             │
│   Ajoutez des films à vos   │
│         favoris             │
│                             │
│   [  Parcourir le catalogue │
│          ]                  │
│                             │
└─────────────────────────────┘
   EmptyStateScreen
```

---

## 🔧 Outils et utilitaires

### UIText - Textes type-safe

```kotlin
// String resource
UIText.Resource(Res.string.my_string)

// String resource avec arguments
UIText.Resource(Res.string.greeting, listOf("John"))

// Plain string
UIText.Plain("Hello World")

// Utilisation
@Composable
fun MyComposable(text: UIText) {
    Text(text.asString())
}
```

### UIErrorHint - Configuration d'erreur

```kotlin
UIErrorHint(
    retry = RetryPolicy.Manual // Manual, Auto, Never
)
```

### EmptyStateConfig - Configuration d'empty state

```kotlin
EmptyStateConfig(
    icon = Icons.Outlined.MovieFilter,
    title = UIText.Resource(Res.string.no_movies),
    description = UIText.Resource(Res.string.no_movies_description),
    ctaText = UIText.Resource(Res.string.browse),
    ctaAction = { /* Action */ }
)
```

---

## ✅ Checklist complète

### Pour chaque nouvel écran

**ViewModel :**
- [ ] Déclare `StateFlow<UiState<T>>`
- [ ] Implémente `override val screenState`
- [ ] Gère les 4 états : Loading, Success, Error, Empty
- [ ] Utilise `sendEvent()` pour les événements one-time
- [ ] Ajoute `UIErrorHint` dans les erreurs

**Screen :**
- [ ] Utilise `StateContainer`
- [ ] Définit `onLoading`, `onSuccess`, `onError`, `onEmpty`
- [ ] Collecte les `events` avec `LaunchedEffect`
- [ ] Utilise `SnackbarHost` pour les messages
- [ ] Utilise les composants de loading appropriés (shimmer)

**Ressources :**
- [ ] Ajoute les strings nécessaires dans `strings.xml`
- [ ] Configure les icônes pour empty states

---

## 🎓 Bonnes pratiques

1. **Toujours gérer Empty** : Ne pas oublier le cas où la liste est vide
2. **Utiliser des shimmer** : Meilleure UX que les loaders simples
3. **Mapper les erreurs** : Utiliser `ErrorMessageMapper` pour des messages cohérents
4. **Séparer état et événements** : State pour données, Events pour actions one-time
5. **Configurer les empty states** : Adapter icône et message au contexte
6. **Tester tous les états** : Loading, Success, Error, Empty

---

## 🆘 Support

**Problème avec un composant ?** → Voir `GUIDE_STATES_USAGE.md`

**Comment migrer ?** → Voir `MIGRATION_EXAMPLE.md`

**Cas spécifique ?** → Voir `EXEMPLES_CAS_USAGE.md`

**Démarrage rapide ?** → Voir `QUICK_START.md`

---

## 📊 Récapitulatif

| ❓ Question | ✅ Réponse |
|-------------|-----------|
| Comment afficher un loader ? | `onLoading = { LoadingCircularProgress() }` |
| Comment gérer une liste vide ? | `onEmpty = { EmptyStateScreen(...) }` |
| Comment afficher une erreur ? | `onError = { error, hint -> ErrorStateScreen(...) }` |
| Comment naviguer ? | `sendEvent(UiEvent.Navigate(route))` |
| Comment afficher un message ? | `sendEvent(UiEvent.ShowSnackBar(message))` |
| Comment faire un overlay loading ? | `LoadingOverlay(visible = isLoading)` |
| Comment faire un shimmer ? | `MovieListLoadingScreen(itemCount = 5)` |

---

**Prêt à commencer ?** → Ouvrez `QUICK_START.md` 🚀
