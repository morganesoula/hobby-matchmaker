---
name: kmp-error-handling
description: |
  Error handling architecture for HobbyMatchmaker KMP — AppError sealed hierarchy, AppResult railway-oriented programming, safeCall/safeNetworkCall/safeCallStorage wrappers, DefaultErrorMessageMapper, UIErrorHint/RetryPolicy, AppError.hint(), AppError.route(), and AppResult.toUIState(). Use this skill whenever defining error types, wrapping network/storage calls, chaining operations with flatMap, mapping errors to UIText, configuring retry policies, or wiring error flow from data source through to UiState. Trigger on phrases like "AppResult", "AppError", "safeCall", "safeNetworkCall", "safeCallStorage", "ErrorMessageMapper", "UIErrorHint", "RetryPolicy", "flatMap", "toUIState", "onFailure", "onSuccess", or "error handling".
---

# Error Handling Architecture — HobbyMatchmaker KMP

## Overview

Errors flow in one direction through a typed pipeline:

```
Exception (platform)
  → safeCall / safeNetworkCall / safeCallStorage
    → AppResult<T, AppError>
      → flatMap chains in repository
        → ViewModel: onFailure / toUIState
          → UiState.Error(UIText, UIErrorHint)
            → Composable renders error UI
```

Never throw exceptions across layer boundaries. Never expose raw exceptions to domain or presentation layers.

---

## AppError — Error Type Hierarchy

**File:** `core/common/…/AppError.kt`

```kotlin
sealed interface AppError {

    sealed interface Network : AppError {
        data object Timeout       : Network
        data object Unreachable   : Network
        data object Canceled      : Network
        data object Serialization : Network
        data class  Http(val code: Int, val body: String? = null) : Network
        data class  Unknown(val cause: Throwable? = null) : Network
    }

    sealed interface Domain : AppError {
        data object Unauthorized              : Domain
        data object Forbidden                 : Domain
        data object NotFound                  : Domain
        data class  Validation(val reason: String) : Domain
    }

    sealed interface Storage : AppError {
        data object WriteFailed : Storage
        data object ReadFailed  : Storage
        data object Corrupted   : Storage
    }

    sealed interface Authentication : AppError {
        data object AlreadyExists      : Authentication
        data object Unknown            : Authentication
        data object InvalidCredentials : Authentication
    }

    sealed interface External : AppError {
        data class Service(val provider: String, val message: String? = null) : External
    }
}
```

**Rules:**
- Never define one-off error strings — add a variant to the appropriate sealed interface.
- Never use `AppError.Network.Unknown` if a more specific variant exists.
- `AppError.Domain.Validation` carries a `reason` string for debug context only — never show it directly in UI.

---

## AppResult — Railway-Oriented Programming

**File:** `core/common/…/AppErrorHelper.kt`

```kotlin
sealed class AppResult<out S, out E> {
    data class Success<S>(val data: S) : AppResult<S, Nothing>()
    data class Failure<E>(val error: E) : AppResult<Nothing, E>()
}
```

### Extension functions

| Function | Purpose |
|---|---|
| `onSuccess(block)` | Side-effect on success; returns `this` for chaining |
| `onFailure(block)` | Side-effect on failure; returns `this` for chaining |
| `mapSuccess(transform)` | Transform the success value |
| `mapError(transform)` | Transform the error value |
| `flatMap(transform)` | Chain a synchronous operation that also returns `AppResult` |
| `flatMapSuspend(transform)` | Chain a suspend operation that also returns `AppResult` |
| `requireNonNull(onNull)` | Lift `AppResult<T?, E>` → `AppResult<T, E>`, fail if null |

```kotlin
// Chaining — short-circuits on first Failure
movieRemoteDataSource.refreshMovies(language)
    .flatMap { movies ->
        movieLocalDataSource.upsertAll(movies.map { it.toMovieDataModel() })
    }
    .flatMap {
        AppResult.Success(Unit)
    }

// Side effects
result
    .onSuccess { data -> _state.update { UiState.Success(data) } }
    .onFailure { error -> eventHandler.sendEvent(UiEvent.ShowSnackBar(mapper.toUIText(error))) }
```

**Rule:** Always use `flatMap` / `flatMapSuspend` to chain dependent operations. Never nest `when (result)` blocks.

---

## Safe Call Wrappers

Three wrappers catch platform exceptions and convert them to `AppResult<T, AppError>`. Always use the correct one for the backend.

### `safeCall` — Ktor HTTP calls

**File:** `core/common/…/AppErrorMapper.kt`

```kotlin
suspend inline fun <Entry> safeCall(
    crossinline block: suspend () -> Entry
): AppResult<Entry, AppError>
```

Maps exceptions to `AppError.Network.*`:
- `CancellationException` → re-thrown (never swallowed)
- `TimeoutCancellationException` → `AppError.Network.Timeout`
- `SerializationException` → `AppError.Network.Serialization`
- `ClientRequestException` / `ServerResponseException` → `AppError.Network.Http(code, body)`
- `IOException` → `AppError.Network.Unreachable`
- `IllegalArgumentException` → `AppError.Domain.Validation(message)`
- Other → `AppError.Network.Unknown(cause)`

```kotlin
// In a Ktor service
override suspend fun getMoviesByPopularityDesc(
    language: String,
    page: Int
): AppResult<MovieResponseRemoteModel, AppError> = safeCall {
    client.request {
        method = HttpMethod.Get
        url { encodedPath = "movie/popular" }
        parameter(PARAMS_LANGUAGE, language)
        parameter(PARAMS_PAGE, page)
    }.body<MovieResponseRemoteModel>()
}
```

### `safeNetworkCall` — Firestore / Firebase calls

**File:** `core/network/…/NetworkConnectivityChecker.kt`

```kotlin
suspend inline fun <Entry> safeNetworkCall(
    connectivityChecker: NetworkConnectivityChecker,
    crossinline block: suspend () -> Entry
): AppResult<Entry, AppError>
```

1. Checks `connectivityChecker.hasActiveConnection()` first → returns `AppError.Network.Unreachable` immediately if offline (fail-fast).
2. Delegates to `safeFirebaseCall` which parses Firebase-specific exceptions:
   - `FirebaseNetworkException` → `AppError.Network.Unreachable`
   - `FirebaseTooManyRequestsException` → `AppError.Network.Http(429)`
   - `FirebaseFirestoreException.NOT_FOUND` → `AppError.Domain.NotFound`
   - `FirebaseFirestoreException.ALREADY_EXISTS` → `AppError.Authentication.AlreadyExists`
   - `FirebaseFirestoreException.PERMISSION_DENIED` → `AppError.Domain.Forbidden`
   - `FirebaseFirestoreException.UNAUTHENTICATED` → `AppError.Domain.Unauthorized`
   - Auth regex patterns → `AppError.Authentication.*` (email exists, wrong password, user not found, etc.)

```kotlin
// In a remote data source (Firestore)
override suspend fun updateUserFavoriteMovieList(
    uuidUser: String,
    movieId: Long,
    isFavorite: Boolean
): AppResult<Unit, AppError> = safeNetworkCall(connectivityChecker) {
    if (isFavorite) {
        firestore.collection(FirestoreUsersCollection).document(uuidUser)
            .set(mapOf("movies" to FieldValue.arrayUnion(movieId)), merge = true)
    } else {
        firestore.collection(FirestoreUsersCollection).document(uuidUser)
            .set(mapOf("movies" to FieldValue.arrayRemove(movieId)), merge = true)
    }
}
```

### `safeCallStorage` — SQLDelight / local DB calls

**File:** `core/common/…/AppErrorMapper.kt`

```kotlin
suspend inline fun <Entry> safeCallStorage(
    crossinline block: suspend () -> Entry
): AppResult<Entry, AppError>
```

Maps exceptions to `AppError.Storage.*`:
- `SerializationException` → `AppError.Storage.Corrupted`
- `IOException` → `AppError.Storage.ReadFailed`
- `IllegalArgumentException` → `AppError.Domain.Validation(message)`
- Other → `AppError.Storage.ReadFailed`

```kotlin
// In a local data source (SQLDelight)
override suspend fun updateMovieWithFavoriteValue(
    id: Long,
    isFavorite: Boolean
): AppResult<Unit, AppError> = safeCallStorage {
    val flag = if (isFavorite) 1L else 0L
    movieDAO.updateMovieFavorite(id, flag)
}

override suspend fun upsertAll(
    movies: List<MovieLocalDataModel>
): AppResult<Unit, AppError> = safeCallStorage {
    movieDAO.upsertMovies(movies.map { it.toMovie() })
}
```

---

## Repository Error Composition

Repositories chain data source calls with `flatMap`. The first `Failure` short-circuits the chain.

```kotlin
// Pattern: network → local → image download
override suspend fun refreshMovies(language: String): AppResult<Unit, AppError> =
    movieRemoteDataSource.refreshMovies(language).flatMap { movies ->
        movieLocalDataSource.upsertAll(movies.map { it.toMovieDataModel() }).flatMap {
            downloadAndUpdateImages(movies)
            AppResult.Success(Unit)
        }
    }

// Pattern: network → local → preferences update → return result
override suspend fun loadMoreMovies(
    language: String,
    page: Int
): AppResult<PaginationInfoDomainModel, AppError> =
    movieRemoteDataSource.fetchMoviesPage(language, page).flatMap { paginatedResult ->
        movieLocalDataSource.upsertAll(paginatedResult.movies.map { it.toMovieDataModel() })
            .flatMap {
                movieSyncPreferences.setLastSyncTimestamp(...)
                AppResult.Success(
                    PaginationInfoDomainModel(
                        currentPage = paginatedResult.currentPage,
                        totalPages = paginatedResult.totalPages,
                        hasMore = paginatedResult.hasMore
                    )
                )
            }
    }
```

---

## UI Error Layer

### UIText — Localized error messages

**File:** `core/design/…/ErrorMessageMapper.kt`

```kotlin
@Immutable
sealed interface UIText {
    data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UIText
    data class Plain(val value: String) : UIText
}
```

### ErrorMessageMapper / DefaultErrorMessageMapper

`DefaultErrorMessageMapper` (singleton `object`) maps every `AppError` variant to a `UIText.Resource`. Injected into every ViewModel.

```kotlin
// In ViewModel — map error to user-visible text
eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))

// Or update UiState directly
_state.update { UiState.Error(defaultMessageMapper.toUIText(error), error.hint()) }
```

`DefaultErrorMessageMapper` is registered as a singleton in Koin:
```kotlin
// core/design/…/di/DiModule.kt
single(createdAtStart = true) { DefaultErrorMessageMapper } bind ErrorMessageMapper::class
```

Always inject `ErrorMessageMapper` (interface), not the concrete `DefaultErrorMessageMapper`.

### UIErrorHint & RetryPolicy

**File:** `core/design/…/AppErrorUIPolicy.kt`

```kotlin
enum class RetryPolicy { Manual, Never, WithBackoff }

data class UIErrorHint(
    val retry: RetryPolicy = RetryPolicy.Never,
    val isUserActionable: Boolean = false
)
```

`UiState.Error` always carries both `UIText` and `UIErrorHint`:

```kotlin
// core/design/…/UiState.kt
data class Error(
    val error: UIText,
    val hint: UIErrorHint = UIErrorHint()
) : UiState<Nothing>
```

### AppError.hint() — Retry policy per error type

**File:** `core/design/…/AppErrorUIPolicy.kt`

```kotlin
fun AppError.hint(): UIErrorHint = when (this) {
    AppError.Network.Timeout      -> UIErrorHint(RetryPolicy.WithBackoff, false)
    AppError.Network.Unreachable  -> UIErrorHint(RetryPolicy.WithBackoff, false)
    is AppError.Network.Http      -> UIErrorHint(
        retry = if (code in 500..599) RetryPolicy.WithBackoff else RetryPolicy.Never,
        isUserActionable = code in listOf(401, 403)
    )
    AppError.Network.Canceled     -> UIErrorHint(RetryPolicy.Never, false)
    AppError.Network.Serialization -> UIErrorHint(RetryPolicy.Never, false)
    is AppError.Network.Unknown   -> UIErrorHint(RetryPolicy.Never, false)

    AppError.Domain.Validation(_) -> UIErrorHint(RetryPolicy.Never, true)
    AppError.Domain.Unauthorized  -> UIErrorHint(RetryPolicy.Never, true)
    AppError.Domain.Forbidden     -> UIErrorHint(RetryPolicy.Never, true)
    AppError.Domain.NotFound      -> UIErrorHint(RetryPolicy.Never, false)

    is AppError.External.Service  -> UIErrorHint(RetryPolicy.WithBackoff, true)

    AppError.Storage.ReadFailed   -> UIErrorHint(RetryPolicy.Never, false)
    AppError.Storage.WriteFailed  -> UIErrorHint(RetryPolicy.WithBackoff, true)
    AppError.Storage.Corrupted    -> UIErrorHint(RetryPolicy.Never, false)

    AppError.Authentication.AlreadyExists      -> UIErrorHint(RetryPolicy.Never, true)
    AppError.Authentication.Unknown            -> UIErrorHint(RetryPolicy.Never, true)
    AppError.Authentication.InvalidCredentials -> UIErrorHint(RetryPolicy.Never, true)
}
```

### AppError.route() — Branch on error intent

Use when you need to route to a specific ViewModel event or UI action based on the error's nature:

```kotlin
inline fun <Event> AppError.route(
    onConnectivity: () -> Event,       // retry possible, not user fault
    onUserActionRequired: () -> Event, // user must act (re-login, fix input)
    onOther: () -> Event               // display and dismiss
): Event
```

```kotlin
// Example in ViewModel
error.route(
    onConnectivity = { showRetryBanner() },
    onUserActionRequired = { navigateToSignIn() },
    onOther = { showSnackbar(defaultMessageMapper.toUIText(error)) }
)
```

### AppResult.toUIState() — Convert result to UiState

**File:** `core/design/…/AppErrorUIPolicy.kt`

```kotlin
fun <T, R> AppResult<T, AppError>.toUIState(
    errorMessageMapper: ErrorMessageMapper,
    mapper: (T) -> UiState<R>
): UiState<R>
```

Used in ViewModels to convert a collected `AppResult` to `UiState` in one step:

```kotlin
// In ViewModel — inline mapper variant
result.toUIState(defaultMessageMapper) { data ->
    when (data) {
        is SomeSuccess.Empty   -> UiState.Empty
        is SomeSuccess.Success -> UiState.Success(data.items.map { it.toUiModel() }.toImmutableList())
    }
}
```

---

## ViewModel Error Handling Patterns

### Pattern 1 — State update on observe

```kotlin
private fun observeMovies() {
    scope.launch {
        catalogOrchestrator.observeMovies().collect { result ->
            when (result) {
                is AppResult.Success -> _movieScreenState.update { mapToUiState(result.data) }
                is AppResult.Failure -> _movieScreenState.update {
                    UiState.Error(
                        defaultMessageMapper.toUIText(result.error),
                        hint = UIErrorHint(retry = RetryPolicy.Manual)
                    )
                }
            }
        }
    }
}
```

### Pattern 2 — Snackbar for secondary/background operations

```kotlin
fun loadMore() {
    scope.launch {
        _paginationState.update { it.copy(isLoadingMore = true) }
        when (val result = catalogOrchestrator.loadMoreMovies(language, nextPage)) {
            is AppResult.Success -> _paginationState.update { it.copy(...) }
            is AppResult.Failure -> {
                _paginationState.update { it.copy(isLoadingMore = false) }
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(result.error))
                )
            }
        }
    }
}
```

### Pattern 3 — onSuccess / onFailure for chaining

```kotlin
private suspend fun toggleFavorite(movieId: Long) {
    userActionOrchestrator.toggleFavorite(movieId)
        .onSuccess { uid ->
            scope.launch { syncFavoriteToCircle(uid, movieId) }
        }
        .onFailure { error ->
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
            )
        }
}
```

### Pattern 4 — Logging-only failures (background refresh)

```kotlin
socialUseCases.refreshIncomingInvitesUseCase(uid)
    .onFailure { error -> Logger.e("Failed to refresh incoming invites: $error") }
```

**Rule:** Use the snackbar event for errors in user-triggered operations. Use logging-only for background/poll refreshes that don't affect primary state.

---

## Summary: Which wrapper to use

| Backend | Wrapper | Error category |
|---|---|---|
| Ktor HTTP (TMDB API) | `safeCall { }` | `AppError.Network.*` |
| Firestore / Firebase Auth | `safeNetworkCall(connectivityChecker) { }` | `AppError.Network.*`, `AppError.Authentication.*`, `AppError.Domain.*` |
| SQLDelight / local DB | `safeCallStorage { }` | `AppError.Storage.*` |

| Operation | AppResult method |
|---|---|
| Chain dependent operations | `flatMap { }` / `flatMapSuspend { }` |
| Transform success data | `mapSuccess { }` |
| Side-effect on success | `onSuccess { }` |
| Side-effect on failure | `onFailure { }` |
| Convert to `UiState` | `toUIState(mapper) { }` |

---

## Checklist: Adding Error Handling to a New Data Path

- [ ] Remote Firestore/Firebase call → wrap with `safeNetworkCall(connectivityChecker) { }`
- [ ] Remote Ktor/HTTP call → wrap with `safeCall { }`
- [ ] Local SQLDelight call → wrap with `safeCallStorage { }`
- [ ] Repository chains multiple calls → use `flatMap` / `flatMapSuspend`
- [ ] ViewModel observes result → use `when (result)` or `.onSuccess` / `.onFailure`
- [ ] Error shown in primary state → `UiState.Error(mapper.toUIText(error), error.hint())`
- [ ] Error shown as snackbar (secondary op) → `UiEvent.ShowSnackBar(mapper.toUIText(error))`
- [ ] Background error → `Logger.e(...)` only, no state update
- [ ] Never use `!!`, never throw generic `Exception`, never expose raw exceptions to domain/UI