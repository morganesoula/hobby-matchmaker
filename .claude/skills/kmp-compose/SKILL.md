---
name: kmp-compose
description: |
  Compose UI patterns, design system, and component library for this KMP project. Use this skill whenever writing composables, choosing a UI component, handling UiState/FormState, wiring ViewModel events in a screen, using the theme or typography, or adding a new screen. Trigger on phrases like "composable", "UI component", "design system", "UiState", "FormState", "UiEvent", "UIText", "atom", "molecule", "organism", "template", "EmptyStateConfig", "StateContainer", "LaunchedEffect", "theme", "typography", or "ErrorMessageMapper". Also covers Compose performance/correctness: collectAsStateWithLifecycle, snapshotFlow, Flow parameters, DisposableEffect, rememberUpdatedState, SideEffect, Modifier order (padding/clickable/clip/background/graphicsLayer), BoxWithConstraints, SubcomposeLayout, nested Scaffold, recomposition, and jank. Covers the Google Compose-performance rules too: strong skipping, stability, @Immutable/@Stable, ImmutableList, phase deferral, backwards writes, derivedStateOf, remember keys, LazyColumn/LazyRow key and contentType, onGloballyPositioned/onVisibilityChanged, animation and graphicsLayer, drawWithCache, AsyncImage, Modifier.Node/Modifier.composed, and auditing existing code for performance.
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

`events` is a one-shot stream, not state, so `LaunchedEffect` + `collect` is correct here; never use it for `StateFlow`s. If the handler calls a callback parameter that can change (e.g. `onNavigate`), read it through `rememberUpdatedState`. Otherwise the long-lived effect keeps the first lambda (see `references/side-effects.md`).

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
    items: List<Item>,
    // 2. State params
    state: UiState<Data>,
    // 3. Event callbacks (prefix with `on`)
    onEvent: (MyEvent) -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    // 4. Modifier last, always defaulted
    modifier: Modifier = Modifier
)
```

**Collections & stability (Strong Skipping is on: Kotlin 2.4, Compose compiler plugin):**
- New code uses plain `List<T>` for list params and UI-model fields. Strong Skipping already skips a composable when the list instance is unchanged (`===`). `ImmutableList` / `@Immutable` force `.equals()` on every recomposition, which is O(N) for collections.
- Never add `@Immutable` / `@Stable` to a model that holds a `List`/`Set`/`Map`, and never add `ImmutableList` for performance.
- Flat `data class`es of `val` primitives/`String`s in a Compose module are already inferred stable. Don't annotate them.
- Add `@Stable`/`@Immutable` only when the compiler can't infer stability (class from a non-Compose module such as `:domain`, or an interface), the class is small and flat with no collections, and an upstream source re-allocates equal instances on each emission.
- Existing `ImmutableList` / `@Immutable` code stays as it is. Only remove it when the model feeds a lazy layout with a large or deeply nested dataset (e.g. `MovieCarousel`'s infinite list); see `references/state-and-stability.md` §4.
- Keep list instances stable on the ViewModel side: `_state.update { it.copy(…) }` reuses untouched list references, so Strong Skipping works.

**State collection:** Use `collectAsStateWithLifecycle()`, which pauses collection while the screen is in the background. It comes from `org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose` and works in `commonMain`. Existing `collectAsState()` calls are legacy: migrate them when you touch a screen.
```kotlin
import androidx.lifecycle.compose.collectAsStateWithLifecycle

val state by viewModel.screenState.collectAsStateWithLifecycle()
```
Collect at the call site (`AppNavHost` / `*Screen`) and pass the resolved value down. **Never** give a composable a `Flow<T>` parameter.

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

## Performance & Correctness Rules (summary)

These are the rules from `references/` that matter most for this codebase. Apply them by default. Open a reference file only when the routing table below says you need its detail.

**Flows**
- `collectAsStateWithLifecycle()` for every flow coming from outside composition. No `Flow<T>` composable params.
- High-frequency sources (more than one emission per ~100 ms): add `.conflate().distinctUntilChanged()` inside `remember(key) { }`.
- To go from Compose state to a side effect (analytics, pagination triggers off `LazyListState`), use `LaunchedEffect(listState) { snapshotFlow { … }.distinctUntilChanged().collect { … } }`.

**Effects**
- Suspending work → `LaunchedEffect(keys)`. Key the effect on every value that should restart it.
- Listener/observer with teardown → `DisposableEffect(key) { …; onDispose { … } }`, not `LaunchedEffect { try/finally }`.
- A callback used inside a long-lived effect → `val latest by rememberUpdatedState(callback)`.
- `SideEffect` only for thin writes to non-Compose objects. Never run side effects inside `remember { }`.
- One-shot screen work (initial load) belongs in the ViewModel, not in a composable effect.

**Modifiers** (read the chain top to bottom: each modifier wraps everything below it)
- `background(color, shape)` rather than the `clip(shape).background(color)` pair. If you do need `clip`, put it before `background`.
- `padding(…).clickable { }` = only the visible area is tappable. `clickable { }.padding(…)` = the padding is tappable too (use this for 48dp touch targets).
- `graphicsLayer { alpha / transforms }` near the top of the chain when it must affect the whole unit.
- Don't wrap a `Modifier` chain in `remember` "for perf".

**Layout / subcomposition**
- No `BoxWithConstraints` just to read a size → use `Modifier.onSizeChanged` / `Modifier.layout`.
- No `BoxWithConstraints` inside lazy list or grid items. Read the size once above the list and pass the result down.
- `Scaffold`s are subcomposing layouts. Only nest them on purpose; the current pattern is `MainScaffold` (bottom bar) → tab screen `Scaffold` (top bar). Don't add a third level, and don't nest them inside list items.
- Avoid `onGloballyPositioned`: use `Modifier.onVisibilityChanged` (viewport/impressions), `onPlaced` / `onLayoutRectChanged` (local bounds). Never store coordinates in `MutableState` to position other content; use a custom `Layout`.
- No decorative wrapper `Box`/`Canvas`: attach the modifier or use `Modifier.drawBehind` / `drawWithCache`.

**State & phases**
- Read fast-changing state (scroll offset, animated alpha/scale/offset) inside lambdas: `Modifier.graphicsLayer { }`, `Modifier.offset { IntOffset(…) }`, `Modifier.layout { }`, or pass `() -> Float` providers to children. This skips composition entirely.
- No backwards writes: never write a state after reading it in the same pass, or from layout/draw into state read in composition (`references/backwards-write.md`).
- `remember(keys)` lists every input it reads. Only `remember` heavy allocations or identity-sensitive objects; don't `remember { mutableStateOf(x) }` just to overwrite it.
- `remember { derivedStateOf { } }` for filtering fast-changing inputs (`listState.firstVisibleItemIndex > 0`). `computedStateOf` needs Compose 1.13; this project is on Compose 1.12, so use `derivedStateOf`. Plain getters for trivial expressions; read `list.size` directly.
- Don't over-index on recomposition counts: make composables cheap first, then measure.

**Lazy layouts**
- Primitive `key` + `contentType` on every lazy `items`. `itemsIndexed` instead of `list.indexOf(item)`.
- Resolve `CompositionLocal`s and collect flows once above the list, pass values down.
- No `LaunchedEffect`/`DisposableEffect` inside items for impressions/analytics (items are pre-fetched): use `Modifier.onVisibilityChanged`.
- Selection: `Set` in state (or `mutableStateSetOf`) and `isSelected: () -> Boolean` providers.

**Effects (additions)**
- Synchronous reactions → `SideEffect(keys) { }` (Compose 1.12+), not a `LaunchedEffect` without suspension or a `DisposableEffect` with an empty `onDispose`.
- Combine several `LaunchedEffect(Unit)` into one with nested `launch { }`. Guard optional effects with `if (callback != null)`.
- `Handler.postDelayed` / `TimerTask` → `LaunchedEffect` + `delay()`.

**Graphics & images**
- Network images: Coil 3 `AsyncImage` (already a dependency), not `Image` + manual loading.
- Cache `Path`/`Brush`/`Paint` in `Modifier.drawWithCache`; `Path.rewind()` over `reset()` in hot loops.
- New custom modifiers use `Modifier.Node` (`ModifierNodeElement`), never `Modifier.composed` (`references/custom-modifiers.md`). `atoms/Images.kt` still uses `composed`/`onGloballyPositioned`: migrate when you touch it.

**Before profiling (Android only):** measure a release build with R8 enabled (see `/r8-analyzer`), never a debuggable build. Baseline Profiles are not set up yet.

## References — load on demand

| Read this file | When |
|---|---|
| `references/collecting-flows.md` | Migrating `collectAsState` calls, sensor/location/high-frequency flows, `minActiveState`, `snapshotFlow` vs `derivedStateOf`, a battery drain or background-work report |
| `references/side-effects.md` | Choosing between `LaunchedEffect`, `DisposableEffect`, `SideEffect`, `RememberedEffect`; an effect that restarts unexpectedly, a stale callback, a leaked listener, a ViewModel per list row |
| `references/modifier.md` | Click or ripple area wrong, background/clip painted in the wrong place, fade/alpha applied to the wrong part, `size` vs `padding` order, reviewing a diff that reorders modifiers |
| `references/subcomposition.md` | `BoxWithConstraints`, `SubcomposeLayout`, `Scaffold` cost, a slow first frame, extra measure passes, scroll jank after wrapping items, `SubcomposeSlotReusePolicy` / `precompose` |
| `references/state-and-stability.md` | Stability / `@Immutable` / `ImmutableList` questions, strong skipping, phase deferral, `derivedStateOf`, `remember` keys, `SnapshotStateList`, `neverEqualPolicy` |
| `references/strong-skipping.md` | How Strong Skipping decides to skip, lambda memoization, opting a composable out |
| `references/backwards-write.md` | Infinite recomposition, first-frame "pop", state written during layout/draw |
| `references/lazy-layouts.md` | Any `LazyColumn`/`LazyRow`/grid/pager: keys, `contentType`, selection, CompositionLocals or flows inside items, impressions |
| `references/layouts.md` | `onGloballyPositioned`, `onVisibilityChanged`, `onPlaced`, floating labels/tooltips, wrapper depth, intrinsics |
| `references/animation.md` | Animating alpha/scale/offset/size, `animateBounds`, shared elements, `Animatable` in a `Modifier.Node` |
| `references/effects-and-threading.md` | Heavy work or allocations in a composable body, `SideEffect(keys)`, combining effects, effect ordering, timers |
| `references/graphics-performance.md` | `Canvas`/`drawWithCache`, shaders, `Path` reuse, primitive collections, `AsyncImage` vs `Image` |
| `references/custom-modifiers.md` | Writing a custom modifier, migrating `Modifier.composed` to `Modifier.Node` |

**Performance audit of existing code:** scan the target against every "Performance & Correctness" group above. Before flagging or editing anything in a group, read its reference file for the approved fix and its scope guards; don't fix from the summary bullets alone.

Notes on these files:
- `collecting-flows`, `side-effects`, `modifier`, `subcomposition` are adapted from skydoves' Compose-performance skills. The other files come from Google's `android/skills` `jetpack-compose-performance` skill (Apache 2.0, merged here on 2026-10-29). Two of its sections were replaced by pointers: lifecycle-aware collection (our rule is stricter) and stale captures (covered by `side-effects.md`).
- They are written Android-first. In this KMP project:
  - DI is Koin (`koinViewModel`).
  - `compose-effects` (`RememberedEffect`, `ViewModelStoreScope`) is not a dependency.
  - Compose is 1.12 (CMP 1.12.x): `SideEffect(keys)` is available, `computedStateOf` (1.13) is not.
  - `Dispatchers.IO` in `commonMain` needs `import kotlinx.coroutines.IO`. Blocking I/O belongs in the data layer (`/kmp-data-layer`), not in a composable's `produceState`.
  - Android-only: `LocalContext`, `BroadcastReceiver`, `Handler`, Perfetto, Layout Inspector, `@TraceRecomposition`, `FrameTimingMetric`, Baseline Profiles.
  - Where these files contradict the rules in this SKILL.md, this SKILL.md wins.

---

## Checklist: Adding a New Screen

- [ ] Create `*Content` or `*Screen` composable in `:features:<name>:presentation`
- [ ] Accept `state: UiState<T>` (or feature-specific state model), `onEvent`, `onNavigate` lambdas, `modifier: Modifier = Modifier`
- [ ] Use `StateContainer` or `when (state)` to branch on `Loading / Empty / Error / Success`
- [ ] Add `LaunchedEffect(Unit)` to collect `viewModel.events`, handle `UiEvent.Navigate`, `UiEvent.ShowSnackBar`, etc.
- [ ] Collect ViewModel state with `collectAsStateWithLifecycle()` at the call site; no `Flow<T>` params
- [ ] Use `remember { SnackbarHostState() }` inside the composable (Compose-internal state)
- [ ] Check modifier order on clickable and clipped elements; no `BoxWithConstraints` in list items
- [ ] Plain `List<T>` params; no new `@Immutable`/`ImmutableList` on models holding collections
- [ ] Every `LazyColumn`/`LazyRow`/grid `items` call has a primitive `key` **and** a `contentType`; small bounded lists (≈ ≤ 10) use `Column`/`Row`
- [ ] Fast-changing values (scroll, animation) are read in lambdas (`graphicsLayer { }`, `offset { }`), not in composition
- [ ] Use `Res.string.*` for all user-visible strings
- [ ] Use design system atoms/molecules — don't rebuild existing components
- [ ] Reach for a template if a similar page layout already exists