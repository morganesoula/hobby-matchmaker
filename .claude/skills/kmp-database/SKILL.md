---
name: kmp-database
description: |
  Local (SQLDelight) and remote (Firestore + Firebase Storage) database reference for HobbyMatchmaker KMP. Use this skill whenever adding a new SQL table or query, writing a Firestore read/write, referencing a collection name or document field, using FieldValue operations, writing a Firestore transaction, uploading to Firebase Storage, or understanding where a piece of data lives. Trigger on phrases like "add a table", "new SQL query", "Firestore collection", "document field", "add to circle", "social invite", "user profile sync", "avatar upload", "FieldValue", "arrayUnion", "transaction", "pseudoLowercase", or "where does X live".
---

# Database Reference — HobbyMatchmaker KMP

Two storage backends, one source of truth rule: **SQLDelight is always the source of truth for the UI**. Firestore is the remote authority synced into SQLDelight; the app never reads Firestore directly for display.

---

## Quick Reference — Where Does Each Entity Live?

| Entity | SQLDelight table | Firestore path | Storage |
|---|---|---|---|
| User profile | `user_profile` | `users/{uid}` (nested under `information`) | — |
| Social circle members | `social_circle_member` | `users/{uid}/circle/{memberUid}` | — |
| Social invitations | `social_invitation` | `socialInvites/{fromUid}_{toPseudo}` | — |
| Cached friend profiles | `user_cache` | `users/{uid}` (batch read by UID) | — |
| Movies (catalogue) | `movie` | — (TMDB only, no Firestore) | — |
| Actors / cast | `actor`, `movie_actor_cross_ref` | — | — |
| Genres | `genre` | — | — |
| Avatar image | `user_profile.avatar_url` (URL only) | — | `avatars/{uid}/avatar.jpg` |
| Liked movie IDs (remote) | `movie.isFavorite` | `users/{uid}.movies` (array of Long) | — |

---

## Part 1 — SQLDelight (Local Database)

### Schema file location

```
core/database/src/commonMain/sqldelight/com/msoula/hobbymatchmaker/core/database/hmm_database.sq
```

All tables and queries live in this single file. DAOs (generated wrappers) live in `core/database`.

---

### Tables

#### `movie`

```sql
CREATE TABLE movie (
    movieId          INTEGER NOT NULL PRIMARY KEY,
    title            TEXT,
    posterFileName   TEXT,       -- remote TMDB filename (e.g. "/abc.jpg")
    synopsis         TEXT,
    releaseDate      TEXT,
    genres           TEXT,       -- stored as JSON string
    localCoverFilePath TEXT,     -- absolute path to cached image on device
    isFavorite       INTEGER DEFAULT 0,
    isSeen           INTEGER DEFAULT 0,
    popularity       REAL    DEFAULT 0.0,
    status           TEXT,
    videoKey         TEXT,       -- YouTube/Vimeo trailer key
    duration         INTEGER,    -- in minutes
    note             REAL,       -- TMDB vote average
    has_cast         INTEGER NOT NULL DEFAULT 0
);
```

- `isFavorite` and `isSeen` are stored as `0`/`1` integers (SQLDelight has no Boolean type).
- `genres` is a JSON-encoded `List<String>` — use the mapper to encode/decode.
- `localCoverFilePath` is `null` until the image is downloaded and cached.
- Index: `movie_is_favorite ON movie(isFavorite)`, `idx_movie_id ON movie(movieId)`.

#### `genre`

```sql
CREATE TABLE genre (
    genreId INTEGER NOT NULL PRIMARY KEY,
    name    TEXT
);
```

Lookup table — rarely queried directly; genre names are denormalized into `movie.genres` JSON.

#### `actor` + `movie_actor_cross_ref`

```sql
CREATE TABLE actor (
    actorId INTEGER NOT NULL PRIMARY KEY,
    name    TEXT,
    role    TEXT    -- character name for this movie
);

CREATE TABLE movie_actor_cross_ref (
    movieId INTEGER NOT NULL,
    actorId INTEGER NOT NULL,
    FOREIGN KEY(movieId) REFERENCES movie(movieId) ON DELETE CASCADE,
    FOREIGN KEY(actorId) REFERENCES actor(actorId) ON DELETE CASCADE,
    PRIMARY KEY(movieId, actorId)
);
```

Many-to-many join. Use `observeDetailMovie` query (LEFT JOIN) to fetch movie + cast together.

#### `user_profile`

```sql
CREATE TABLE user_profile (
    uid             TEXT NOT NULL PRIMARY KEY,
    name            TEXT NOT NULL,
    pseudo          TEXT NOT NULL,
    avatar_url      TEXT,                           -- remote download URL
    bio             TEXT,
    interests_json  TEXT AS List<String> NOT NULL,  -- JSON-encoded list
    liked_count     INTEGER NOT NULL,
    circle_count    INTEGER NOT NULL,
    updated_at      INTEGER NOT NULL                -- epoch millis
);
```

One row per signed-in user. `interests_json` uses a SQLDelight column adapter (`List<String>`).

#### `social_circle_member`

```sql
CREATE TABLE social_circle_member (
    uid              TEXT NOT NULL,
    member_uid       TEXT NOT NULL,
    member_name      TEXT,
    member_pseudo    TEXT,
    member_avatar_url TEXT,
    added_at         INTEGER NOT NULL,   -- epoch millis
    PRIMARY KEY (uid, member_uid)
);
```

Composite primary key — `(uid, member_uid)` pair is unique. Synced from `users/{uid}/circle` subcollection.
Index: `idx_social_circle_member_uid`, `idx_social_circle_member_composite`.

#### `user_cache`

```sql
CREATE TABLE user_cache (
    uid               TEXT NOT NULL PRIMARY KEY,
    pseudo            TEXT NOT NULL,
    name              TEXT,
    avatar_url        TEXT,
    movies_liked_json TEXT NOT NULL DEFAULT '[]',  -- JSON-encoded List<Long>
    updated_at        INTEGER NOT NULL
);
```

Lightweight cache for friend profiles (used by movie matching: "friends who also liked this"). `movies_liked_json` is a JSON-encoded `List<Long>` of TMDB movie IDs.

#### `social_invitation`

```sql
CREATE TABLE social_invitation (
    id          TEXT NOT NULL PRIMARY KEY,  -- same composite ID as Firestore doc
    from_uid    TEXT NOT NULL,
    from_pseudo TEXT,
    to_pseudo   TEXT,
    name        TEXT,
    status      TEXT NOT NULL,              -- "PENDING" | "ACCEPTED" | "DECLINED"
    created_at  INTEGER NOT NULL,           -- epoch millis
    updated_at  INTEGER                     -- epoch millis, nullable
);
```

---

### Key Queries Reference

| Query name | Table | Description |
|---|---|---|
| `observeMovies` | `movie` | All movies ordered by `popularity DESC` |
| `observeDetailMovie` | `movie` + `actor` | Full detail with LEFT JOIN cast |
| `observeFavoriteMovies` | `movie` | Only `isFavorite = 1`, ordered by popularity |
| `observeFavoriteMoviesCount` | `movie` | `COUNT(*)` where `isFavorite = 1` |
| `getFavoriteIds` | `movie` | Only `movieId` for all favorites |
| `upsertMoviePreservingUserData` | `movie` | Updates catalogue fields without touching `isFavorite` / `isSeen` |
| `selectUserProfileWithMembersById` | `user_profile` + `social_circle_member` | Full profile with LEFT JOIN circle |
| `observeIncomingInvitations` | `social_invitation` | Where `to_pseudo = ?` |
| `observeSentInvitations` | `social_invitation` | Where `from_uid = ?` |
| `observeUsersByUids` | `user_cache` | Batch lookup by UID list |

---

### Adding a New Table or Query

1. Edit `core/database/src/commonMain/sqldelight/com/msoula/hobbymatchmaker/core/database/hmm_database.sq`.
2. Add `CREATE TABLE` definition and all queries in that same file.
3. Create a DAO interface + implementation in `core/database/src/commonMain/kotlin/…/core/database/`.
4. Register the DAO in the `coreModuleDAO` Koin module.
5. Inject the DAO interface into the feature `LocalDataSourceImpl` — never import the SQLDelight-generated type outside `core:database`.

> **Do not** create separate `.sq` files per feature — the project uses a single shared schema file.

---

## Part 2 — Firestore (Remote Database)

### Collection constants

Always use the constants — never hardcode collection names:

```kotlin
// core/common/src/commonMain/kotlin/.../core/common/data/FirestoreVariables.kt
const val FirestoreUsersCollection = "users"
const val FirestoreCircleCollection = "circle"
```

---

### Collection: `users`

**Document ID:** Firebase Auth UID (`String`)

| Field | Type | Notes |
|---|---|---|
| `uid` | `String` | Mirrors the document ID |
| `email` | `String` | Set on first sign-in only |
| `information` | `Map` | Nested object — see sub-fields below |
| `information.pseudo` | `String` | Display pseudo (original casing) |
| `information.pseudoLowercase` | `String` | `.lowercase()` of pseudo — used for all search queries |
| `information.name` | `String?` | Full name |
| `information.avatarUrl` | `String?` | Firebase Storage download URL |
| `information.bio` | `String?` | User biography |
| `information.interests` | `List<String>?` | List of interest tags |
| `movies` | `List<Long>` | TMDB movie IDs the user has liked (managed via `FieldValue.arrayUnion/arrayRemove`) |

**Write pattern — always use `merge = true`** to avoid clobbering other fields:

```kotlin
firestore.collection(FirestoreUsersCollection)
    .document(uid)
    .set(mapOf("information" to informationMap), merge = true)
```

---

### Subcollection: `users/{uid}/circle`

**Document ID:** UID of the circle member (`String`)

| Field | Type | Notes |
|---|---|---|
| `addedAt` | `Timestamp` | Use `FieldValue.serverTimestamp` on write |
| `memberPseudo` | `String` | Pseudo of the member |
| `username` | `String?` | Full name of the member |
| `avatarUrl` | `String?` | Download URL |
| `commonMoviesCount` | `Int` | Count of shared liked movies |
| `moviesLiked` | `List<Long>?` | TMDB IDs the member has liked |

**Accessed via:**

```kotlin
firestore.collection(FirestoreUsersCollection)
    .document(uid)
    .collection(FirestoreCircleCollection)
    .document(memberUid)
```

Circle entries are added **atomically** alongside invite acceptance — never insert a circle entry without also updating the invite status in the same transaction (see Transactions section below).

---

### Collection: `socialInvites`

**Document ID pattern:** `"{fromUid}_{toPseudo}"` — deterministic composite key that prevents duplicate invitations between the same pair.

| Field | Type | Notes |
|---|---|---|
| `fromUid` | `String` | Sender's Firebase UID |
| `fromPseudo` | `String` | Sender's pseudo |
| `toPseudo` | `String` | Recipient's pseudo (not UID — search is pseudo-based) |
| `name` | `String?` | Recipient's display name |
| `status` | `String` | `"PENDING"` \| `"ACCEPTED"` \| `"DECLINED"` |
| `createdAt` | `String` | ISO-8601 timestamp string |
| `updatedAt` | `String?` | ISO-8601 timestamp string, nullable |

**Build the document ID consistently:**

```kotlin
val docId = "${fromUid}_${toPseudo}"
```

---

### Query Patterns

#### Pseudo search (range query on `pseudoLowercase`)

```kotlin
firestore.collection(FirestoreUsersCollection)
    .orderBy("information.pseudoLowercase", Direction.ASCENDING)
    .startAtFieldValues(searchTerm.lowercase())
    .endAtFieldValues(searchTerm.lowercase() + "\uf8ff")   // Unicode sentinel for prefix match
    .limit(10)
    .get()
```

> Always search on `pseudoLowercase`, never on `pseudo`. The `\uf8ff` sentinel is a standard Firestore prefix-match trick.

#### Batch fetch by UID list

```kotlin
firestore.collection(FirestoreUsersCollection)
    .where { FieldPath.documentId() inArray uids }
    .get()
```

#### Real-time listener (Flow)

```kotlin
firestore.collection(FirestoreUsersCollection)
    .document(uid)
    .collection(FirestoreCircleCollection)
    .snapshots           // returns Flow<CollectionSnapshot>
    .map { snapshot -> snapshot.documents.map { it.get<T>("field") } }
```

#### FieldValue array operations (movie likes)

```kotlin
// Add a liked movie
firestore.collection(FirestoreUsersCollection)
    .document(uid)
    .update(mapOf("movies" to FieldValue.arrayUnion(movieId)))

// Remove a liked movie
firestore.collection(FirestoreUsersCollection)
    .document(uid)
    .update(mapOf("movies" to FieldValue.arrayRemove(movieId)))
```

Use `FieldValue.arrayUnion` / `arrayRemove` for the `movies` array — never read-modify-write the whole array.

---

### Transactions

Transactions are required when two documents must be updated atomically. The key case is **accepting an invitation**, which must simultaneously:
1. Update `socialInvites/{id}.status` → `"ACCEPTED"`
2. Insert `users/{senderUid}/circle/{recipientUid}`
3. Insert `users/{recipientUid}/circle/{senderUid}`

```kotlin
firestore.runTransaction { transaction ->
    // 1. Update invite status
    transaction.update(inviteRef, mapOf("status" to "ACCEPTED"))
    // 2. Add recipient to sender's circle
    transaction.set(senderCircleRef, senderCircleData)
    // 3. Add sender to recipient's circle
    transaction.set(recipientCircleRef, recipientCircleData)
}
```

> Never accept an invitation outside a transaction. Partial writes would leave the social graph in an inconsistent state.

---

### Social Circle Limit

The social circle is capped at **5 members**. Before sending an invite or accepting one, call `checkSocialCircleLimit(uid)` which counts documents in the `circle` subcollection. Reject the operation with `AppError.Domain.Validation` if the limit is reached.

---

## Part 3 — Firebase Storage

### Bucket

```
gs://hobby-matchmaker.firebasestorage.app
```

### Path structure

| Asset | Path |
|---|---|
| User avatar | `avatars/{uid}/avatar.jpg` |

### Upload pattern

```kotlin
val ref = storage.reference.child("avatars/$uid/avatar.jpg")
ref.putData(imageBytes)
val downloadUrl: String = ref.getDownloadUrl()
```

After a successful upload:
1. Store the `downloadUrl` in `users/{uid}/information.avatarUrl` (Firestore).
2. Store it in `user_profile.avatar_url` (SQLDelight).
3. Clean up the previous local cached file via `ImageFileManager`.

> The app reads the `downloadUrl` (HTTPS), not the `gs://` reference, for display.

---

## Sync Rules (SQLDelight ↔ Firestore)

| Direction | When | Pattern |
|---|---|---|
| Firestore → SQLDelight | Real-time listener fires | `channelFlow` — remote listener writes to local, UI collects local |
| SQLDelight → Firestore | User action (like, profile edit) | Write local first (`safeCallStorage`), then write remote (`safeNetworkCall`) |
| Firestore → SQLDelight (one-shot) | App launch / pull refresh | `refreshMovies`-style: fetch remote, upsert local, return `AppResult<Unit>` |

**Never** read from Firestore in a ViewModel or use case. All UI data flows from SQLDelight.

---

## Checklist: Adding a New Persisted Entity

- [ ] Decide where it lives: SQLDelight only, Firestore only, or both
- [ ] If SQLDelight: add `CREATE TABLE` and all queries to `hmm_database.sq`; create DAO interface + impl in `core:database`
- [ ] If Firestore: use existing collection constants or add a new `const val` to `FirestoreVariables.kt`; document the field names here
- [ ] If both: define the sync direction and pattern (real-time `channelFlow` vs. one-shot refresh)
- [ ] If the entity has an array field mutated concurrently: use `FieldValue.arrayUnion` / `arrayRemove`, never read-modify-write
- [ ] If writing two or more documents that must be consistent: use `firestore.runTransaction`
- [ ] Map remote model → local model → domain model via extension functions in `mappers/`