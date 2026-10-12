# User module

This module manages lightweight user summary data for any user — not just the signed-in one. It is consumed by features that need to display friend profiles (social circle, movie matches). It implements a three-tier caching strategy: in-memory → SQLDelight → Firestore.

## Responsibilities

- Observe a single user or a list of users as a reactive `Flow`
- Fetch user summaries on demand with a cache-first strategy (memory → local DB → remote)
- Batch-fetch multiple users by UID (chunked to stay within Firestore limits)
- Sync and cache `UserSummaryDomainModel` (uid, pseudo, name, avatarUrl, moviesLiked) locally

## Architecture

This module follows a Clean, modular architecture, designed for Kotlin Multiplatform:

- domain/ — `UserDataRepository` interface, `UserSummaryDomainModel`, use cases
- data/ — `UserLocalDataSourceImpl` (SQLDelight `user_cache` table), `UserRemoteDataSourceImpl` (Firestore `users` collection), `UserDataRepositoryImpl` (three-tier cache coordination)

- 🔥 **Firestore** for remote user lookups (single + batch `FieldPath.documentId inArray`)
- 🗄️ **SQLDelight** (`user_cache` table) for local persistence
- 🧩 **Koin** for dependency injection