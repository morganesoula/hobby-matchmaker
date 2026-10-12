# Login module

This module owns the sign-in and sign-up presentation layer: form state, validation, ViewModels, and the orchestrators that chain form validation → authentication → session setup. It does not handle Firebase credentials directly — that responsibility belongs to `core/authentication`.

## Features

- Display and validate sign-in and sign-up forms (email, password, name)
- Orchestrate email/password and social provider sign-in flows (Google, Apple, Facebook)
- Handle first-sign-in setup (default profile creation, session initialisation)
- Manage password reset flow
- Persist "don't ask again" preference for guest-mode dialog

## Architecture

This module follows a Clean, modular architecture, designed for Kotlin Multiplatform:

- domain/ — form validation use cases only (`ValidateEmailUseCase`, `ValidatePasswordUseCase`, `ValidateNameUseCase`, `LoginValidateFormUseCase`)
- presentation/ — `SignInViewModel`, `SignUpViewModel`, orchestrators (`SignInOrchestrator`, `AuthenticationManager`, `SessionManager`), social UI clients (`GoogleUIClient`, `AppleUIClient`, `FacebookUIClient`), `SignInScreen`, `SignUpScreen`

- 🧠 **MVVM** with hybrid MVI event model
- 🧩 **Compose Navigation** for screen transitions
- 🔐 **core/authentication** for credential operations
- 🔄 **core/session** for session state setup after sign-in
- 🧪 **Koin** for dependency injection
- 🖥️ **Platform expect/actual** for social provider UI clients (Android vs iOS)