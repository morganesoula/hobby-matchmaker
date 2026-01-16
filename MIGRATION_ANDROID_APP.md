# Migration AGP 9.0 : androidApp + com.android.kotlin.multiplatform.library

## Objectif

Migrer de :
```
composeApp (KMP + com.android.application)  ❌ Non supporté AGP 9.0+
```

Vers :
```
composeApp (KMP + com.android.kotlin.multiplatform.library)  ✅
androidApp (com.android.application)                          ✅
```

---

## Prérequis

```bash
git checkout -b migration/agp9-kmp
git add . && git commit -m "chore: before AGP 9.0 migration"
```

---

## Étape 1 : Ajouter le nouveau plugin dans libs.versions.toml

```toml
[plugins]
# Ajouter cette ligne
android-kotlin-multiplatform-library = { id = "com.android.kotlin.multiplatform.library", version.ref = "gradle" }
```

---

## Étape 2 : Créer le module androidApp

### 2.1 Créer la structure

```bash
mkdir -p androidApp/src/main/kotlin/com/msoula/hobbymatchmaker
mkdir -p androidApp/src/main/res/values
```

### 2.2 Ajouter dans settings.gradle.kts

```kotlin
include(":androidApp")  // Ajouter après include(":composeApp")
```

### 2.3 Déplacer les fichiers Android

```bash
mv composeApp/src/androidMain/kotlin/com/msoula/hobbymatchmaker/MainActivity.kt \
   androidApp/src/main/kotlin/com/msoula/hobbymatchmaker/

mv composeApp/src/androidMain/kotlin/com/msoula/hobbymatchmaker/HobbyMatchMakerApplication.kt \
   androidApp/src/main/kotlin/com/msoula/hobbymatchmaker/

mv composeApp/src/androidMain/AndroidManifest.xml androidApp/src/main/

# Si des resources Android existent
[ -d "composeApp/src/androidMain/res" ] && cp -r composeApp/src/androidMain/res/* androidApp/src/main/res/
```

### 2.4 Créer androidApp/build.gradle.kts

```kotlin
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.msoula.hobbymatchmaker"
    compileSdk = 35

    val secretsPropertiesFile = rootProject.file("secrets.properties")
    val secretProperties = Properties().apply {
        if (secretsPropertiesFile.exists()) load(FileInputStream(secretsPropertiesFile))
    }

    defaultConfig {
        applicationId = "com.msoula.hobbymatchmaker"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["facebookApplicationID"] = secretProperties["facebook_application_id"] ?: ""
        manifestPlaceholders["facebookClientToken"] = secretProperties["facebook_client_token"] ?: ""
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.appcompat)
    implementation(libs.activity.compose)
    implementation(libs.facebook.android.sdk)
    implementation(libs.play.services.auth)
    implementation(libs.koin.android)
    implementation(libs.timber.android)
}
```

### 2.5 Créer androidApp/src/main/res/values/strings.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">HobbyMatchmaker</string>
</resources>
```

---

## Étape 3 : Migrer composeApp vers le nouveau plugin

### 3.1 Modifier composeApp/build.gradle.kts

**Remplacer les plugins :**
```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)  // ✅ Nouveau
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kover)
    alias(libs.plugins.spm.kmp)
    alias(libs.plugins.build.konfig)
}
```

**Remplacer le bloc kotlin { } :**
```kotlin
kotlin {
    // ✅ Remplace androidTarget par androidLibrary
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.composeapp"
        compileSdk = 35
        minSdk = 26

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    // iOS (inchangé)
    val xcf = XCFramework()
    iosArm64().apply {
        compilations["main"].cinterops.create("nativeIosShared")
        binaries.framework {
            baseName = "composeApp"
            isStatic = true
            xcf.add(this)
        }
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
        }

        commonMain.dependencies {
            // Modules (inchangés)
            implementation(project(Modules.AUTHENTICATION_DATA))
            // ... tous les autres modules
        }

        androidMain.dependencies {
            api(libs.findLibrary("appcompat").get())
            implementation(libs.findLibrary("activity-compose").get())
            implementation(libs.findLibrary("facebook-android-sdk").get())
            implementation(libs.findLibrary("play-services-auth").get())
            implementation(libs.findLibrary("koin-android").get())
            implementation(libs.findLibrary("timber-android").get())
        }
    }
}
```

**Supprimer le bloc android { } externe** - tout est maintenant dans `kotlin { androidLibrary { } }`

**Garder le reste inchangé** (buildkonfig, compose.resources, swiftPackageConfig)

---

## Étape 4 : Migrer les convention plugins

### 4.1 Supprimer ou renommer ApplicationConventionPlugin

Ce plugin appliquait `com.android.application` - il n'est plus nécessaire pour composeApp.

**Option A** : Supprimer le plugin et configurer composeApp explicitement (recommandé)

**Option B** : Renommer en `ComposeAppConventionPlugin` et adapter pour le nouveau plugin

### 4.2 Modifier MultiplatformConventionPlugin (pour core/* et features/*)

**Fichier :** `build-logic/convention/src/main/java/MultiplatformConventionPlugin.kt`

```kotlin
class MultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.kotlin.multiplatform.library")  // ✅ Nouveau
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                androidLibrary {
                    compileSdk = ProjectConfig.PROJECT_CONFIG_SDK_VERSION
                    minSdk = ProjectConfig.PROJECT_CONFIG_MIN_SDK_VERSION

                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_21)
                    }
                }
                // ... reste de la config
            }
        }
    }
}
```

**Note :** Le namespace doit être défini par chaque module individuellement.

---

## Étape 5 : Tester

```bash
./gradlew clean
./gradlew --stop
./gradlew :composeApp:build
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

Vérifier :
- ✅ Plus de warning de dépréciation KMP/AGP
- ✅ L'app fonctionne normalement

---

## Problèmes courants

| Erreur | Solution |
|--------|----------|
| `androidLibrary block not found` | Vérifier que `com.android.kotlin.multiplatform.library` est appliqué |
| `Cannot configure android extension` | Supprimer le bloc `android {}` externe, utiliser `kotlin { androidLibrary {} }` |
| `Unresolved reference` dans androidApp | Ajouter `implementation(project(":composeApp"))` |
| Resources Android non trouvées | Activer avec `androidLibrary { androidResources { enable = true } }` |
| Tests ne compilent pas | Activer avec `withHostTest {}` ou `withDeviceTest {}` |

---

## Limitations du nouveau plugin

| Non supporté | Alternative |
|--------------|-------------|
| Build variants | Module `com.android.library` séparé |
| Product flavors | Module `com.android.library` séparé |
| BuildConfig natif | BuildKonfig (déjà en place ✅) |
| Data/View Binding | Compose Multiplatform (déjà en place ✅) |

---

## Fonctionnalités opt-in

```kotlin
androidLibrary {
    withJava()                    // Compilation Java
    androidResources { enable = true }  // Resources Android
    withHostTest { }              // Tests unitaires
    withDeviceTest { }            // Tests instrumentés
}
```

---

## Commit

```bash
git add .
git commit -m "refactor: migrate to AGP 9.0 with androidApp + KMP library plugin"
```

---

## Ressources

- [Android KMP Plugin](https://developer.android.com/kotlin/multiplatform/plugin)
- [Kotlin Multiplatform Compatibility Guide](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html)