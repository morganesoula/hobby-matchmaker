# 🎯 HobbyMatchMaker -- Projet en cours d'exécution

**HobbyMatchMaker** est une application mobile multiplateforme (Android & iOS) développée avec Kotlin Multiplatform. L'application permet aux utilisateurs de se connecter autour de passions communes comme le cinéma, les jeux ou d'autres loisirs.

---

## ⚙️ Fonctionnalités

- Authentification via Google, Apple, Facebook (via Firebase)
- Affichage de films populaires via l'API TMDB
- Système de like et de match autour des passions
- Interface partagée grâce à Compose Multiplatform
- Navigation via Decompose

---

## 🧱 Architecture

Le projet suit une architecture **modulaire et Clean**, pensée pour Kotlin Multiplatform :
composeApp/ # Application Android (Jetpack Compose) 
iosApp/ # Application iOS (Swift/SwiftUI) 

Pour chaque module, vous retrouvez les trois couches principales: data, domain et presentation (seulement quand c'est utile)
domain/ # 100% commonMain (logique métier pure) 
data/ # commonMain + androidMain + iosMain 
presentation/ # UI & logique d'affichage multiplateforme


- 🧠 **MVVM** pour la séparation des responsabilités
- 🧩 **Decompose** pour la navigation multiplateforme
- 🧪 **Koin** pour l'injection de dépendances
- 🌐 **Ktor** pour les appels réseau

---

## 🚀 Lancer le projet

### 🧰 Prérequis

- [Android Studio](https://developer.android.com/studio) Flamingo ou supérieur
- [Xcode](https://developer.apple.com/xcode/) 15 ou supérieur
- [Swift Package Manager](https://www.swift.org/package-manager/)
- Kotlin 1.9+

### 🔑 Configuration requise

Avant de cloner et de lancer le projet, il est nécessaire de configurer les **clés API** :

Crée un fichier `local.properties` à la racine du projet (ou configure les variables d’environnement) :

TMDB_API_KEY=your_tmdb_key
FIREBASE_API_KEY=your_firebase_key
FIREBASE_PROJECT_ID=your_project_id

---

## 🧪 Tests

⚠️ Les tests sont actuellement en cours de réécriture suite à la migration vers Kotlin Multiplatform.
Ils seront disponibles dans une prochaine version du projet.

---

## 📦 Technologies utilisées
Kotlin Multiplatform

- Jetpack Compose / Compose Multiplatform
- Decompose (navigation)
- Koin (injection de dépendances)
- Ktor (client HTTP)
- Firebase Auth (via dev.gitlive.firebase)
- TMDB API
- Kotlinx Serialization, Coroutines & DateTime

---

## 🛑 Contribution
Ce projet est open-source, mais les contributions externes ne sont pas acceptées.
C’est un projet personnel destiné à expérimenter et explorer les possibilités de KMM.

---

## 📄 Licence
Ce projet est sous licence MIT.
