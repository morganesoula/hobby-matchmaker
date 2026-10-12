# Common module

Foundation layer shared by every other module. It also provides network infrastructure shared by all features: connectivity checking, the `safeNetworkCall` wrapper, and the Koin module that bootstraps Firebase and JSON serialisation. Contains cross-cutting concerns — error types, result handling, safe-call wrappers, logging, and shared utilities. Has no dependency on any other `core/` module.

## Responsibilities

- Define `AppError` (sealed hierarchy) and `AppResult<S, E>` used across all layers
- Provide safe-call wrappers: `safeNetworkCall` (Firestore/Ktor + connectivity check), `safeCallStorage` (SQLDelight), `safeLocalWrite` (DataStore)
- Provide `AppResult` extension functions: `flatMap`, `mapSuccess`, `onSuccess`, `onFailure`
- Platform-independent `Logger` with `LogLevel` and `LogFormatter` (expect/actual for Android/iOS)
- `DispatcherProvider` abstraction for coroutine dispatchers (testability)
- `ImageFileManager` for reading/writing image files on device (expect/actual)
- `DataStoreHelper` / `CreateDataStore` factory for DataStore<Preferences> instances
- `FirestoreVariables` constants (`FirestoreUsersCollection`, `FirestoreCircleCollection`)
- Shared `ValidationResult` and `UseCaseParameters` wrapper types
- Expose `NetworkConnectivityChecker` — platform-specific check for active network connection (Android: `ConnectivityManager`, iOS: `NetworkReachability`)
- Provide `safeNetworkCall(connectivityChecker) { ... }` — checks connectivity, runs the block, maps exceptions to `AppError.Network`
- Bootstrap `FirebaseAuth`, `Json` (kotlinx.serialization, lenient + ignoreUnknownKeys), and `Dispatchers.IO` as Koin singletons

## Architecture

This module has no domain/data split — it is a flat utility module:

- commonMain/ — all shared types and interfaces, `NetworkConnectivityChecker` interface, `NetworkModule` Koin module
- androidMain/ — Android implementations (Logger, ImageFileManager, Utility, `NetworkConnectivityChecker` impl using `ConnectivityManager`)
- iosMain/ — iOS implementations (Logger, ImageFileManager, Utility, `NetworkConnectivityChecker` impl using Swift `NetworkReachability` interop)

- 🔥 **Firebase KMP** (auth instance wiring)
- 🧩 **Koin** for dependency injection
- 📡 **kotlinx.serialization** for JSON configuration
- 💾 **DataStore Preferences** for key-value persistence helpers
