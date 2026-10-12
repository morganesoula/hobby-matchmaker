---
name: kmp-data-layer
description: |
  Data layer patterns for this KMP project — data sources, repositories, DTOs/data models, mappers, SQLDelight DAOs, Ktor/Firestore remote calls, safe-call helpers, and offline-first flow. Use this skill whenever writing or reviewing a data source or repository, creating data models or mappers, handling network/storage errors, or implementing a local+remote sync pattern. Trigger on phrases like "create a repository", "create a data source", "add a DAO", "write a mapper", "remote data model", "local data model", "network call", "offline-first", "safeNetworkCall", "safeCallStorage", or "AppResult".
---

# Data Layer Architecture — HobbyMatchmaker KMP

This project follows **strict Clean Architecture**: the `domain` module holds interfaces and domain models, the `data` module holds all implementations. Neither layer may reference the other's concrete types — only the domain contracts.

---

## Layer Responsibilities

| Layer | Module | Contains |
|---|---|---|
| Domain | `:features:<name>:domain` | Repository interfaces, domain models, use case classes |
| Data | `:features:<name>:data` | Repository impls, local/remote data sources, data models, mappers |
| DB (shared) | `:core:database` | SQLDelight schema, all DAOs (interface + impl) |
| Network (shared) | `:core:network` | Ktor `HttpClient`, `safeNetworkCall`, `NetworkConnectivityChecker` |
| Common (shared) | `:core:common` | `AppResult`, `AppError`, `safeCallStorage`, `Logger`, extensions |

---

## `AppResult` and `AppError`

All data operations return `AppResult<Success, AppError>`. Never throw exceptions across layer boundaries; map them to `AppResult.Failure`.

```kotlin
// core/common
sealed class AppResult<out Success, out Error> {
    data class Success<S>(val data: S) : AppResult<S, Nothing>()
    data class Failure<E>(val error: E) : AppResult<Nothing, E>()
}

sealed interface AppError {
    sealed interface Network : AppError   // Timeout, Unreachable, Http, Unknown
    sealed interface Domain : AppError    // Unauthorized, Validation
    sealed interface Storage : AppError   // WriteFailed, ReadFailed
    sealed interface Authentication : AppError
    sealed interface External : AppError
}
```

**Available extension functions on `AppResult`:**

```kotlin
result.flatMap { value -> AppResult.Success(transform(value)) }
result.mapSuccess { value -> transform(value) }   // maps the Success branch
```

---

## Safe-Call Helpers

### Remote calls — `safeNetworkCall`

Wraps Ktor/Firestore calls. Always pass the `NetworkConnectivityChecker` to get an `AppError.Network` instead of a raw exception.

```kotlin
// In a remote data source
override suspend fun syncUserProfile(model: UserProfileRemoteDataModel): AppResult<Unit, AppError> =
    safeNetworkCall(connectivityChecker) {
        firestore.collection(FirestoreUsersCollection)
            .document(model.uid)
            .set(mapOf("information" to model.toMap()), merge = true)
    }
```

### Local/storage calls — `safeCallStorage`

Wraps SQLDelight DAO calls. Returns `AppResult.Failure(AppError.Storage.*)` on exception.

```kotlin
// In a local data source
override suspend fun upsertUserProfile(model: UserProfileLocalDataModel): AppResult<Unit, AppError> =
    safeCallStorage {
        userProfileDAO.upsertUserProfile(model.toUserProfileDataEntity())
    }
```

---

## Domain Layer Contracts

### Repository interface (in `:features:<name>:domain`)

- Pure Kotlin — no Android, Ktor, or Firebase imports.
- Reactive queries return `Flow<T>` (never suspend).
- One-shot writes/reads return `AppResult<Success, AppError>`.

```kotlin
interface MovieRepository {
    // Reactive — Flow, no suspend
    fun observeMovies(): Flow<List<MovieDomainModel>>
    fun observeFavoriteMovies(): Flow<List<FavoriteMovieDomainModel>>
    fun observeLikedMoviesIds(): Flow<List<Long>>

    // One-shot — suspend + AppResult
    suspend fun refreshMovies(language: String): AppResult<Unit, AppError>
    suspend fun updateMovieFavoriteLocal(id: Long, isFavorite: Boolean): AppResult<Unit, AppError>
    suspend fun loadMoreMovies(language: String, page: Int): AppResult<PaginationInfoDomainModel, AppError>
}
```

### Domain models (in `:features:<name>:domain`)

- Immutable `data class`.
- All fields have defaults; expose a `companion object { val Initial = … }` for blank state.
- Never nullable collections; use `emptyList()` as the default.
- Nullable fields only for genuinely optional values (e.g. `avatarUrl: String?`).

```kotlin
data class MovieDomainModel(
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
        val Initial = MovieDomainModel()
    }
}
```

### Use cases (in `:features:<name>:domain`)

Single-responsibility, thin wrappers around repository calls. Operator `invoke` is the entry point.

```kotlin
class ObserveFavoriteMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(): Flow<List<FavoriteMovieDomainModel>> =
        movieRepository.observeFavoriteMovies()
}
```

---

## Data Layer Structure

Within `:features:<name>:data`:

```
data/
├── dataSources/
│   ├── local/
│   │   ├── models/         ← local data models (*LocalDataModel)
│   │   ├── mappers/        ← local mappers (entity ↔ local model ↔ domain model)
│   │   ├── *LocalDataSource.kt          ← interface
│   │   └── *LocalDataSourceImpl.kt      ← implementation
│   └── remote/
│       ├── models/         ← network/Firestore data models (*RemoteDataModel)
│       ├── mappers/        ← remote mappers (remote model → domain model)
│       ├── services/       ← Ktor service interfaces + impls (when used)
│       ├── *RemoteDataSource.kt         ← interface
│       └── *RemoteDataSourceImpl.kt     ← implementation
├── repositories/
│   └── *RepositoryImpl.kt               ← coordinates local + remote
└── models/                              ← shared data models if needed across sources
```

---

## Data Models (DTOs)

Three distinct model types, never mixed across layers:

| Model type | Suffix | Layer | Used for |
|---|---|---|---|
| Remote data model | `*RemoteDataModel` | `data` only | Ktor JSON responses, Firestore documents |
| Local data model | `*LocalDataModel` | `data` only | Intermediate between DB entity and domain |
| Domain model | `*DomainModel` | `domain` | Exposed to use cases and ViewModels |

Remote models use `@Serializable` + `@SerialName` for Ktor/JSON deserialization:

```kotlin
@Serializable
data class MovieRemoteDataModel(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("poster_path") val poster: String? = null,
    @SerialName("vote_average") val note: Double? = null
)
```

Firestore/non-serialized remote models are plain `data class` (no annotations):

```kotlin
data class SocialCircleEntryRemoteDataModel(
    val memberUid: String = "",
    val memberPseudo: String = "",
    val commonMoviesCount: Int = 0,
    val moviesLiked: List<Long>? = null
)
```

---

## Mappers

Mappers are **extension functions** defined in the `mappers/` subfolder alongside the source model. Each mapper file is named after what it maps from.

```kotlin
// dataSources/local/mappers/MovieLocalMappers.kt
fun Movie.toMovieDataModel(): MovieLocalDataModel = MovieLocalDataModel(
    id = id,
    title = title ?: MovieLocalDataModel.Initial.title,
    remotePoster = remotePoster ?: MovieLocalDataModel.Initial.remotePoster,
    isFavorite = isFavorite == 1L,
    // ...
)

fun MovieLocalDataModel.toMovieDomainModel(): MovieDomainModel = MovieDomainModel(
    id = id,
    title = title,
    coverFileName = remotePoster,
    localCoverFilePath = localPoster ?: "",
    isFavorite = isFavorite,
    // ...
)
```

```kotlin
// dataSources/remote/mappers/MovieRemoteMappers.kt
fun MovieRemoteDataModel.toMovieDataModel(): MovieLocalDataModel = MovieLocalDataModel(
    id = id?.toLong() ?: 0L,
    title = title ?: "",
    remotePoster = poster ?: "",
    // ...
)
```

**Rules:**
- Mappers never call repository or use case methods.
- Mappers live in `data`, never in `domain`.
- One file per direction is fine; don't create a single god-mapper class.

---

## Repository Implementations

Repositories coordinate local and remote data sources. Common patterns:

### Pattern 1 — Local-first with remote refresh (offline-first)

```kotlin
class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieSyncPreferences: MovieSyncPreferences,
    private val imageRepository: ImageRepository
) : MovieRepository {

    // Always read from local DB (single source of truth)
    override fun observeMovies(): Flow<List<MovieDomainModel>> =
        movieLocalDataSource.observeMovies()
            .map { list -> list.map { it.toMovieDomainModel() } }

    // Refresh = fetch remote → persist local
    override suspend fun refreshMovies(language: String): AppResult<Unit, AppError> =
        movieRemoteDataSource.refreshMovies(language).flatMap { movies ->
            movieLocalDataSource.upsertAll(movies.map { it.toMovieDataModel() })
        }
}
```

### Pattern 2 — Dual-stream with `channelFlow` (remote realtime + local cache)

Used when Firestore provides real-time updates AND SQLDelight must stay in sync:

```kotlin
override fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>> =
    channelFlow {
        // Launch Firestore listener → write to local DB
        launch {
            socialRemoteDataSource.observeSocialCircle(uid)
                .collectLatest { remoteMembers ->
                    socialLocalDataSource.syncCircle(mapRemoteToLocal(remoteMembers, uid))
                }
        }
        // Collect local DB → emit to consumer
        socialLocalDataSource.observeSocialCircle()
            .map { list -> list.map { it.toDomainModel() } }
            .collect { send(it) }
    }
```

### Pattern 3 — Remote-only (no local cache)

```kotlin
override suspend fun checkIfPseudoIsAvailable(pseudo: String): AppResult<Boolean, AppError> =
    userProfileRemoteDataSource.checkIfPseudoIsAvailable(pseudo)
```

---

## SQLDelight DAOs (`:core:database`)

DAOs live exclusively in `:core:database`. Feature data modules inject DAO interfaces as constructor parameters; they never depend on the SQL implementation directly.

- `observe*()` → returns `Flow<List<Entity>>` for reactive queries
- `insert*()` / `upsert*()` / `update*()` / `delete*()` → `suspend` write operations
- Entities are SQLDelight-generated types; never leak them outside `core:database`

```kotlin
// Feature data source references the DAO interface, not the SQLDelight impl
class MovieLocalDataSourceImpl(private val movieDAO: MovieDAOImpl) : MovieLocalDataSource {
    override fun observeMovies(): Flow<List<MovieLocalDataModel>> =
        movieDAO.observeMovies().map { movies -> movies.map { it.toMovieDataModel() } }
}
```

---

## Remote Sources — Two Backends

This project uses **two remote backends** in the same data layer:

| Backend | Used for | Access pattern |
|---|---|---|
| **Firestore** (Firebase KMP) | User profiles, social circle, invitations | `firestore.collection(…).document(…)` + `snapshots` Flow for realtime |
| **Ktor** (TMDB API) | Movie catalogue | `tmdbKtorService.getMoviesByPopularityDesc(…)` → returns `AppResult` |

Both share `safeNetworkCall(connectivityChecker)` as the error boundary. Firestore constants (collection names) live in `core:common`:

```kotlin
// core/common — shared Firestore collection names
const val FirestoreUsersCollection = "users"
const val FirestoreCircleCollection = "circle"
```

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Repository interface | `*Repository` | `MovieRepository` |
| Repository impl | `*RepositoryImpl` | `MovieRepositoryImpl` |
| Local data source interface | `*LocalDataSource` | `MovieLocalDataSource` |
| Local data source impl | `*LocalDataSourceImpl` | `MovieLocalDataSourceImpl` |
| Remote data source interface | `*RemoteDataSource` | `MovieRemoteDataSource` |
| Remote data source impl | `*RemoteDataSourceImpl` | `MovieRemoteDataSourceImpl` |
| Ktor API service interface | `*KtorService` | `TMDBKtorService` |
| Ktor API service impl | `*KtorServiceImpl` | `TMDBKtorServiceImpl` |
| Network/Firestore DTO | `*RemoteDataModel` | `MovieRemoteDataModel` |
| Local intermediate model | `*LocalDataModel` | `MovieLocalDataModel` |
| Domain model | `*DomainModel` | `MovieDomainModel` |
| Mapper (extension fun) | `fun Source.toTarget()` | `fun MovieRemoteDataModel.toMovieDataModel()` |

---

## Checklist: Adding a New Feature Data Layer

- [ ] Define domain model(s) in `:features:<name>:domain/models/`
- [ ] Define repository interface in `:features:<name>:domain/repositories/`
- [ ] Define remote data model(s) (`*RemoteDataModel`) in `:features:<name>:data/dataSources/remote/models/`
- [ ] Define local data model(s) (`*LocalDataModel`) in `:features:<name>:data/dataSources/local/models/`
- [ ] Write mapper extension functions in `dataSources/local/mappers/` and `dataSources/remote/mappers/`
- [ ] If DB access is needed: add SQL queries to `core/database/hmm_database.sq`, create DAO interface + impl in `:core:database`, register in `coreModuleDAO`
- [ ] Implement `*LocalDataSourceImpl` wrapping the DAO, using `safeCallStorage`
- [ ] Implement `*RemoteDataSourceImpl` using `safeNetworkCall(connectivityChecker)`
- [ ] Implement `*RepositoryImpl` combining local + remote, implementing the domain interface
- [ ] Add DI bindings in `:features:<name>:data/di/` and register in `appModule()` in `KoinInit.kt`