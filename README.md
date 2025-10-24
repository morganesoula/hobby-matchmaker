# 🎯 HobbyMatchMaker — Project in Progress

**HobbyMatchMaker** is a cross-platform mobile application (Android & iOS) built with **Kotlin Multiplatform**.
It allows users to connect around shared interests such as movies, games (upcoming), and other hobbies (upcoming).

---

## ⚙️ Features

- Authentication via **Google**, **Apple**, and **Facebook** (powered by Firebase)
- Display of popular movies using the **TMDB API**
- Like and match system around shared interests
- Shared UI built with **Compose Multiplatform**
- Shared navigation with **Compose Navigation**
- Image handling with **Coil 3** and per-request listeners for debugging

---

## 🧱 Architecture

The project follows a **Clean, modular architecture**, designed for Kotlin Multiplatform:
- composeApp/ # Android application (Jetpack Compose)
- iosApp/ # iOS application (Swift)
- core/ # Core logic (authentication, design, database, etc.)
- features/ # Feature modules (movies, movie detail, etc.)


Each feature or core module typically follows the three-layer pattern:
- domain/ # 100% commonMain (pure business logic)
- data/ # commonMain + androidMain + iosMain (repositories, data sources)
- presentation/ # Multiplatform UI & presentation logic (when applicable)


- 🧠 **MVVM** for clear separation of concerns
- 🧩 **Compose Navigation (in progress)** for multiplatform navigation
- 🧪 **Koin** for dependency injection
- 🌐 **Ktor** for network calls

---

## 🚀 Getting Started

### 🧰 Requirements

- [Android Studio](https://developer.android.com/studio) **Narwhal or newer**
- [Xcode](https://developer.apple.com/xcode/) **16 or newer**
- [Swift Package Manager](https://www.swift.org/package-manager/)
- **Kotlin 2.1+**

---

### 🔑 API & Secret Configuration

Before running the project, you must configure your API keys (Firebase & TMDB).

#### Android
Create a `local.properties` file at the project root (or set environment variables):
- TMDB_API_KEY=your_tmdb_key
- FIREBASE_API_KEY=your_firebase_key
- FIREBASE_PROJECT_ID=your_project_id


#### iOS
Create a `secrets.xcconfig` file under the `iosApp/` folder (not versioned) and include it in your build configuration:

```xcconfig```
- TMDB_API_KEY = your_tmdb_key
- GOOGLE_REVERSED_CLIENT_ID = your_google_reversed_client_id
- FIREBASE_APP_ID = your_firebase_app_id

---

## 🧪 Tests

Unit and integration tests are being rewritten following the Kotlin Multiplatform migration.
They will be re-enabled progressively in upcoming releases. You can find them in the root folder tests/

---

## 📦 Tech Stack

### Languages & Frameworks

- Kotlin Multiplatform
- Jetpack Compose / Compose Multiplatform
- Swift (for iOS setup)

#### Libraries

- Compose Navigation Multiplatform (in migration)
- Koin — Dependency Injection
- Ktor — HTTP Client
- Firebase Auth (via dev.gitlive.firebase)
- TMDB API
- Coil 3 — Image loading
- Kotlinx Coroutines, Serialization & DateTime

---

## 🛑 Contribution

This project is open-source, but external contributions are not accepted.
It is a personal project meant for experimentation and exploration of Kotlin Multiplatform capabilities.

---

## 📄 License

This project is licensed under the MIT License.
