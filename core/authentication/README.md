# Authentication module

This module handles all Firebase-based credential operations: account creation, sign-in (email/password and social providers), account linking, and password reset. It owns no UI — the presentation layer lives in `core/login`.

## Responsibilities

- Create accounts and sign in with email/password
- Sign in with social providers (Google, Apple, Facebook) via `AuthProvider` abstraction
- Link a social credential to an existing account
- Send password reset emails
- Check whether the current sign-in is the user's first (`IsFirstSignInUseCase`, currently unused:
  first-sign-in setup is driven by `HydrateUserProfileUseCase` finding no pseudo)
- Expose the provider display name on `AuthenticatedUser` (used to prefill the first name)
- Fetch current Firebase auth state

## Architecture

This module follows a Clean, modular architecture, designed for Kotlin Multiplatform:

- domain/ — repository interface, use cases, auth state models (`AuthState`, `AuthenticatedUser`, `ProviderType`)
- data/ — `AuthenticationRepositoryImpl`, `AuthenticationRemoteDataSourceImpl`, platform-specific `AuthProvider` implementations (Google, Apple, Facebook)

- 🔐 **Firebase Authentication** for credential management
- 🧩 **Koin** for dependency injection
- 🧪 **Platform expect/actual** for provider-specific credential builders (Android vs iOS)