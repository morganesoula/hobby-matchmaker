# Session module

This module manages the user's session state across app launches: connectivity flag, current user UUID, guest-mode preferences, and initial Firestore user document creation. It is the source of truth for "is someone logged in, and who?"

## Responsibilities

- Persist and observe whether the user is connected (`IS_CONNECTED_KEY`) via DataStore
- Store and expose the current user's UID (`USER_PROFILE_UUID`) locally
- Expose `SessionState` — `Guest(uid)` or `Authenticated(uid)` — as a reactive stream
- Persist the "don't ask again" checkbox value for the guest-mode dialog
- Create the initial user document in Firestore (`users/{uid}`) on first sign-in

## Architecture

This module follows a Clean, modular architecture, designed for Kotlin Multiplatform:

- domain/ — repository interface (`SessionRepository`), `SessionState` model, `SessionUserDomainModel`, and use cases (`ObserveSessionStateUseCase`, `CreateUserUseCase`, `SetIsConnectedUseCase`, `SetCurrentUserProfileUuidUseCase`, etc.)
- data/ — `SessionLocalDataSource` (DataStore), `SessionRemoteDataSource` (Firestore), `SessionRepositoryImpl`

- 💾 **DataStore Preferences** for local session flags
- 🔥 **Firestore** for initial user document creation
- 🧩 **Koin** for dependency injection