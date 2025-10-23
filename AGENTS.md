# 🧠 Gemini Project Context — HobbyMatchMaker

## 🎯 Project Overview
**HobbyMatchMaker** is a **Kotlin Multiplatform Mobile (KMP)** app (Android + iOS) that helps users connect around shared hobbies such as movies or games.

The app lets users:
- Authenticate via **Google**, **Apple**, or **Facebook** using **Firebase Authentication**.
- View popular movies via the **TMDB API**.
- Like movies and see matches with their circle.
- Navigate via **Decompose** (migration to Compose Navigation planned).
- Share UI code between Android and iOS via **Compose Multiplatform**.

---

## 🧱 Architecture
Follows **Clean Architecture + MVVM**:
- **Domain** → Use cases, business logic, pure Kotlin, no platform dependency.
- **Data** → Repository implementations, Firebase (via `dev.gitlive`), Ktor, SQLDelight.
- **Presentation** → ViewModels, UI States, and event channels (Compose).
- **App Layer (composeApp / iosApp)** → Platform entry points and DI setup.

**Layers communicate only through interfaces**.
All modules are designed for testability and KMP compatibility.

---

## 🧩 Modules
:composeApp → Android entry point
:iosApp → iOS entry point
:core:authentication → Auth logic (sign-in, sign-up, reset password)
:core:login → Presentation layer for auth
:core:design → Shared UI components & theme
:core:database → SQLDelight setup
:core:common → Models, Result/AppError handling, utils
:core:session → User session management
:features:movies → Movie list logic (browse, like, match)
:features:moviedetail→ Movie detail screen (trailers, info)

---

## 🧰 Tech Stack
### ✅ Shared (commonMain)
- **Kotlin 2.1.10**
- **Compose Multiplatform 1.9.0**
- **Ktor 3.x** (HTTP client)
- **SQLDelight 2.x**
- **Koin 4.x** (DI)
- **Firebase GitLive SDK**
- **Napier** (logging)
- **kotlinx.datetime** for date/time
- **Result wrapper** with `mapSuccess` / `mapError` extensions

### ✅ Android
- Jetpack Compose
- Coil 3 for image loading (with `ImageRequest.Listener` for debug)
- Firebase Auth + Firestore
- Material3 + dynamic color
- `Decompose` navigation migrating to `Compose Navigation`

### ✅ iOS
- Compose Multiplatform UI
- Firebase via GitLive
- Apple Sign-In via native Swift bridge (`AppleSignInManager`)
- WKWebView for YouTube trailers
- Local caching for images via path storage

---

## 🧠 Design & UX
- UI shared via Compose Multiplatform
- Modern Material 3 look
- Uses consistent paddings, typography, and animations (motion-based)
- Debug UI traces image loading and network calls
- iOS version fully native-feel (SafeArea, bounce scroll, system icons)

---

## 🧪 Testing
- **Kotest 5.9.1**
- **Mockk 1.14.2**
- **Turbine** for Flow tests
- **runTest + advanceUntilIdle()** for coroutine testing
- `Fake` data sources for repositories
- Integration tests focus on `SignIn` and `MovieDetailViewModel`

---

## ⚙️ Build & Gradle
- Uses **Convention Plugins** for KMP configuration (`configureMultiplatform`, `enableIos`)
- `libs.versions.toml` defines dependencies
- Manual `BuildConfigField` in modules for API keys
- Uses Swift Package Manager (no CocoaPods)
- `jvmTarget` = 21

---

## 🔐 Security
- No hardcoded API keys
- Safe calls for Firebase & Ktor
- Result-based error handling
- Separation of sensitive data per platform

---

## 🚀 Goals
1. Finalize Compose Navigation migration.
2. Improve iOS Firebase login (test Google + Apple).
3. Continue integration tests for features.
4. Publish Medium & LinkedIn devlogs documenting progress.
5. Prepare for public beta (2025).

---

## 💬 How to Help Me
When I ask Gemini something, **always**:
- Suggest **KMP-compatible** libraries.
- Avoid deprecated APIs.
- Respect **MVVM + Clean Architecture**.
- Prefer **simple, readable, performant** Kotlin.
- Don’t rewrite files entirely — only show relevant changed parts.

---

### Example prompt to Gemini
> “Optimize this Ktor call using the current error-handling system in HobbyMatchMaker.”
> “Adapt this Jetpack Compose component for iOS using Compose Multiplatform best practices.”
> “Add a unit test for this use case using Kotest and advanceUntilIdle().”

---

*(c) Morgane Soula — HobbyMatchMaker 2025*
