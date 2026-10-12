---
name: kmp-compose
description: |
  Compose UI patterns, design system, and component library for this KMP project. Use this skill whenever writing composables, choosing a UI component, handling UiState/FormState, wiring ViewModel events in a screen, using the theme or typography, or adding a new screen. Trigger on phrases like "composable", "UI component", "design system", "UiState", "FormState", "UiEvent", "UIText", "atom", "molecule", "organism", "template", "EmptyStateConfig", "StateContainer", "LaunchedEffect", "theme", "typography", or "ErrorMessageMapper".
---

# Compose UI & Design System — HobbyMatchmaker KMP

## Core Principle

Composables render state and forward user actions — nothing more. Zero business logic, zero data transformation inside composables. All state lives in the ViewModel as `StateFlow`.

---

## Atomic Design Hierarchy

The design system in `:core:design` follows a strict four-level hierarchy. Always pick the lowest level that works:

```
atoms/       → Primitive building blocks (Button, TextField, Text, Card shell, Loader…)
molecules/   → Named combinations of atoms (MovieCard, TopBar, NoDataCard, InvitationCard…)
organisms/   → Complex, feature-aware layouts (ProfileHeader, SignInForm, MovieDetailInformation…)
templates/   → Full-page slot-based layouts (SignInLayout, HubLayout, EditableProfileLayout…)
```

Feature screens in `:features:*:presentation` compose organisms and templates — they do not reach into atoms unless there is no suitable higher-level component.

---

## Theme

### Applying the Theme

```kotlin
// App.kt — wraps the entire UI
HobbyMatchMakerTheme {
    AppNavHost(socialClients = socialClients)
}
```

`HobbyMatchMakerTheme` applies `darkColorScheme`/`lightColorScheme` based on `isSystemInDarkTheme()`. Always use `MaterialTheme.colorScheme.*` — never hardcode colors.

### Color Palette

| Role | Value | Usage |
|---|---|---|
| `primary` | `#FD9800` warm orange | Buttons, active icons, highlights |
| `secondary` | `#F4B151` light orange | Accents |
| `tertiary` | `#4CAF50` green | Validation, success states |
| `error` | `#ba1a1a` red | Error text, warning boxes |
| `background` / `surface` | Warm dark brown (dark) / light beige (light) | Page & card backgrounds |

Extended colors accessed via helpers:
```kotlin
disabledContainerColor()   // @Composable helper
onDisabledColor()
successContainerColor()
HMMTextFieldColors()       // Custom TextFieldColors for PrimaryTextField
```

### Typography

Font family: **Quicksand** (Regular, Medium, SemiBold, Bold, Light) — loaded from compose resources.

```kotlin
// Use MaterialTheme text styles, not hardcoded sizes
Text("Title",    style = MaterialTheme.typography.headlineSmall)
Text("Body",     style = MaterialTheme.typography.bodyLarge)
Text("Caption",  style = MaterialTheme.typography.bodySmall)
```

### Design Tokens

Use `CustomSize`, `CustomFontSize`, `IconSize`, and `CardSize` objects instead of magic numbers:

```kotlin
// CustomSize
CustomSize.Four   // 4.dp    CustomSize.Eight   // 8.dp
CustomSize.Sixteen // 16.dp  CustomSize.ThirtyTwo // 32.dp
CustomSize.FortyEight // 48.dp  CustomSize.NinetySix // 96.dp

// IconSize
IconSize.TwentyFour   // 24.dp
IconSize.FortyEight   // 48.dp
IconSize.NinetySix    // 96.dp

// CardSize
CardSize.Width300     // 300.dp
CardSize.Height440    // 440.dp
```

---

## UiState — Async Data States

`UiState<T>` is the standard sealed interface for all screen data. Defined in `core/design/util/UiState.kt`.

```kotlin
@Immutable
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class  Success<T>(val data: T) : UiState<T>
    data class  Error(val error: UIText, val hint: UIErrorHint = UIErrorHint()) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}
```

**Rendering pattern — use `StateContainer` atom:**

```kotlin
StateContainer(
    state = movieState,
    onLoading = { MovieListLoadingScreen() },
    onEmpty   = { EmptyStateScreen(emptyConfig) },
    onError   = { error, hint -> ErrorStateScreen(error, hint) },
    onSuccess = { movies -> MovieCarousel(movies) }
)
```

Or inline `when` for one-off screens:
```kotlin
when (val s = movieState) {
    is UiState.Loading  -> LoadingCircularProgress()
    is UiState.Empty    -> EmptyStateScreen(emptyConfig)
    is UiState.Error    -> ErrorStateScreen(s.error, s.hint)
    is UiState.Success  -> MovieCarousel(s.data)
}
```

---

## FormState — Form Validation States

`FormState<T>` models the complete lifecycle of form input. Defined in `core/design/util/UiState.kt`.

```kotlin
sealed interface FormState<out T> {
    data class Idle<T>(val value: T)                                   : FormState<T>
    data class Validation<T>(val value: T)                             : FormState<T>
    data class Invalid<T>(val value: T, val errors: Map<String, String>) : FormState<T>
    data class Submitting<T>(val value: T)                             : FormState<T>
    data class Success<T>(val result: T)                               : FormState<T>
    data class Error<T>(val value: T, val error: UIText)               : FormState<T>
}
```

Use `FormState` for any screen with user input (sign-in, sign-up, profile editing). The ViewModel drives the form state; composables render and dispatch events.

---

## UiEvent — ViewModel → UI Communication

`UiEvent` is the one-way channel from ViewModels to the UI. Defined in `core/design/util/UiEvent.kt`.

```kotlin
@Immutable
sealed interface UiEvent {
    data class ShowSnackBar(val message: UIText) : UiEvent
    data class Navigate(val destination: NavigationDestination) : UiEvent
    data class OnDataReady(val data: String) : UiEvent
    data class OpenDialog(val dialogPurpose: String) : UiEvent
    data class CloseDialog(val dialogName: String) : UiEvent
    data class ShowAnimation(val data: MatchAnimationData) : UiEvent
    data object CapacityReached : UiEvent
}
```

**Collection pattern in composables — always `LaunchedEffect(Unit)`:**

```kotlin
LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
        when (event) {
            is UiEvent.Navigate     -> // call navCallbacks
            is UiEvent.ShowSnackBar -> snackbarHostState.showSnackbar(event.message.asStringSuspend())
            is UiEvent.OnDataReady  -> // handle data
            else -> Unit
        }
    }
}
```

Use `event.message.asStringSuspend()` (suspend extension) inside `LaunchedEffect` / coroutine contexts.
Use `event.message.asString()` (composable extension) inside `@Composable` functions.

---

## UIText — Localized Strings

`UIText` is the sealed type for all user-visible text. Never pass raw `String` across the ViewModel boundary for text that may need localization.

```kotlin
sealed interface UIText {
    data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UIText
    data class Plain(val value: String) : UIText
}

// Emit from ViewModel:
UIText.Resource(Res.string.network_timeout)
UIText.Plain("Dynamic: $value")

// Render in Composable:
Text(uiText.asString())

// In suspend context (LaunchedEffect):
snackbarHostState.showSnackbar(uiText.asStringSuspend())
```

All static strings come from compose resources: `stringResource(Res.string.key)`. The default locale is French; English translations are in `values-en-rUS/strings.xml`.

---

## EventHandler (ViewModel side)

`EventHandler` is the `Channel`-based event bus injected into ViewModels.

```kotlin
class EventHandler {
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    suspend fun sendEvent(event: UiEvent) { _events.send(event) }
}

// ViewModel usage
scope.launch {
    eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.SignIn))
}
```

ViewModels expose `val events: Flow<UiEvent>` — never the `Channel` itself.

---

## Error Handling in UI

### ErrorMessageMapper

`DefaultErrorMessageMapper` (singleton `object`) maps every `AppError` to a `UIText`. Injected into every ViewModel.

```kotlin
// In ViewModel
eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))

// Or update state
_state.update { UiState.Error(defaultMessageMapper.toUIText(error), error.hint()) }
```

### UIErrorHint & RetryPolicy

`error.hint()` (extension on `AppError`) returns a `UIErrorHint` that drives the error screen's retry button:

```kotlin
data class UIErrorHint(
    val retry: RetryPolicy = RetryPolicy.Never,
    val isUserActionable: Boolean = false
)
enum class RetryPolicy { Manual, Never, WithBackoff }

// ErrorStateScreen respects the hint automatically:
UiState.Error(message, error.hint())
```

### EmptyStateConfig

For empty states, construct an `EmptyStateConfig` with icon, title, optional description and CTA:

```kotlin
val config = EmptyStateConfig(
    icon = LucideFilm,
    title = UIText.Resource(Res.string.movies_empty_title),
    description = UIText.Resource(Res.string.movies_empty_description),
    ctaText = UIText.Resource(Res.string.movies_empty_cta),
    ctaAction = { viewModel.onEvent(MovieEventModel.OnRefresh) }
)
EmptyStateScreen(config)
```

---

## Key Components Cheat Sheet

### Atoms

| Component | Purpose | Key params |
|---|---|---|
| `PrimaryButton` | Main CTA | `text`, `onClick`, `loading`, `enabled` |
| `SecondaryButton` | Outlined secondary action | `text`, `onClick`, `loading` |
| `SocialMediaButton` | OAuth provider button | `icon`, `text`, `loading`, `onClick` |
| `PrimaryTextField` | Standard text input (120 char limit) | `value`, `onValueChange`, `label`, `leadingIcon` |
| `PasswordTextField` | Password with toggle visibility | `value`, `onValueChange` |
| `StateContainer<T>` | Switches Loading/Empty/Error/Success | `state`, `onLoading`, `onEmpty`, `onError`, `onSuccess` |
| `GenericCard` | Card container | `modifier`, `containerColor`, `content` |
| `MovieGenericCard` | Parallax carousel card | `state: LazyListState`, `index`, poster content |
| `ShimmerCard` | Animated loading placeholder | `modifier` |
| `LoadingCircularProgress` | Centered spinner | — |
| `LoadingOverlay` | Full-screen translucent loader | — |
| `NoDataText` / `EmptyDataText` | Empty state text | — |
| `ErrorText` | Error-colored text | `text` |
| `FavoriteButton` | Animated like button | `isFavorite`, `onFavoriteClicked` |
| `RatingChip` | Star + score badge | `rating: Double` |
| `SpacerHeight4/8/16/32` | Vertical spacing | — |
| `SpacerWeight1` | Flexible fill spacer | — |

### Molecules

| Component | Purpose |
|---|---|
| `MovieCard` | Interactive carousel card; double-tap → favorite, single-tap → detail |
| `HubFavoriteMovie` | Compact favorite card with optional MATCH badge |
| `TopBar` / `BackNavigationTopBar` / `EditProfileTopBar` | Navigation bars |
| `NoDataCard` | Icon + title + description + optional CTA |
| `InvitationCard` / `ReceivedInvitationCard` | Friend request cards |
| `MemberItem` | Avatar + name + common movies count |
| `TipTextField` / `WarningTextField` | Info/warning hint boxes |
| `ValidationRequirementsList` | Checklist for password/field rules |
| `InterestsBlock` / `InterestDeletableBlock` | Tag display / editable tags |
| `PseudoSearchBar` | SearchBar with dropdown results |
| `LabeledDivider` | Divider with center text label |

### Organisms

| Component | Purpose |
|---|---|
| `MovieCarousel` | Infinite-scroll lazy row with auto load-more |
| `MovieDetailInformation` | Full movie detail with parallax, video player, cast slot |
| `ProfileHeader` | Avatar, name, bio, edit button, add-people form |
| `SignInForm` / `SignUpForm` | Complete auth forms with validation |
| `ProfileEditInformationForm` | Name/pseudo/bio editing with validation |
| `ProfileEditInterestsForm` | Tag management (max 5, no duplicates) |
| `SocialInvitation` | Full sent/received invitations section |

### Templates

Templates use **slot APIs** — each section is a `@Composable () -> Unit` parameter:

```kotlin
// Example usage — assemble organisms into a template
SignInLayout(
    padding = paddingValues,
    topBar = { /* logo composable */ },
    form = { SignInForm(/* … */) },
    divider = { LabeledDivider("ou") },
    socialMediaSection = { SignInSocialMedia(/* … */) },
    bottomSection = { /* sign-up link */ }
)
```

Available templates: `SignInLayout`, `SignUpLayout`, `HubLayout`, `CompleteProfileLayout`, `EditableProfileLayout`, `GuestProfileLayout`, `SocialLayout`.

---

## Composable Naming Conventions

| Level | Convention | Example |
|---|---|---|
| Atom | Descriptive noun | `PrimaryButton`, `ShimmerCard`, `SpacerHeight16` |
| Molecule | Component + context | `MovieCard`, `InvitationCard`, `TopBar` |
| Organism | Feature + role | `MovieDetailInformation`, `ProfileEditInterestsForm` |
| Template | Descriptive + `Layout` | `SignInLayout`, `EditableProfileLayout` |
| Screen (in features) | Feature + `Content` or `Screen` | `MovieContent`, `SocialContent` |

---

## Composable Rules

**Parameter order:**

```kotlin
@Composable
fun MyComponent(
    // 1. Required data params first
    title: String,
    items: ImmutableList<Item>,
    // 2. State params
    state: UiState<Data>,
    // 3. Event callbacks (prefix with `on`)
    onEvent: (MyEvent) -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    // 4. Modifier last, always defaulted
    modifier: Modifier = Modifier
)
```

**Collections:** Use `ImmutableList<T>` (from `kotlinx.collections.immutable`) for list params to avoid unnecessary recompositions. Convert with `.toImmutableList()`.

**Stability:** Annotate data classes passed to composables with `@Immutable` when they contain `List`, `Map`, or other unstable types.

**State collection:** Always use `collectAsState()`:
```kotlin
val state by viewModel.screenState.collectAsState()
```

**Remember:** Only for Compose-internal state (`LazyListState`, `SnackbarHostState`, `PagerState`, animation state). Never for application state — that belongs in the ViewModel.

**No business logic in composables.** No `if (user.role == "admin")` — expose a pre-computed boolean from the ViewModel.

---

## Icons

All icons are custom `ImageVector` objects in `core/design/icons/`. Reference them directly:

```kotlin
Icon(imageVector = LucideFilm, contentDescription = null)
Icon(imageVector = FeatherInbox, contentDescription = stringResource(Res.string.inbox_cd))
```

Available icons include: `Add`, `AddPeople`, `ArrowBack`, `Camera`, `Check`, `Delete`, `Email`, `Favorite`, `FavoriteBorder`, `Heart`, `Lock`, `MovieFilter`, `Person`, `PlayArrow`, `SearchHeart`, `Sparkle`, `StarFull`, `Visibility`, `VisibilityOff`, and 20+ more.

For decorative icons: `contentDescription = null`. For interactive/informational: always provide a localized string.

---

## Checklist: Adding a New Screen

- [ ] Create `*Content` or `*Screen` composable in `:features:<name>:presentation`
- [ ] Accept `state: UiState<T>` (or feature-specific state model), `onEvent`, `onNavigate` lambdas, `modifier: Modifier = Modifier`
- [ ] Use `StateContainer` or `when (state)` to branch on `Loading / Empty / Error / Success`
- [ ] Add `LaunchedEffect(Unit)` to collect `viewModel.events`, handle `UiEvent.Navigate`, `UiEvent.ShowSnackBar`, etc.
- [ ] Use `remember { SnackbarHostState() }` inside the composable (Compose-internal state)
- [ ] Wrap lists in `.toImmutableList()` before passing to child composables
- [ ] Annotate any data class holding unstable fields with `@Immutable`
- [ ] Use `Res.string.*` for all user-visible strings
- [ ] Use design system atoms/molecules — don't rebuild existing components
- [ ] Reach for a template if a similar page layout already exists