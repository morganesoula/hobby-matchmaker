import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    kotlin("multiplatform")
    `android-library`
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)

            // Firebase - through GitLive
            implementation(libs.firebase.kmp.auth)

            // Koin
            implementation(libs.koin.core)

            // Ktor
            implementation(libs.bundles.ktor)

            // Modules
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.COMMON))
        }

        androidMain.dependencies {
            // Google
            implementation(libs.play.services.auth)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.network"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    val secretsPropertiesFile = project.rootProject.file("secrets.properties")
    val secretProperties = Properties()

    if (secretsPropertiesFile.exists()) {
        secretProperties.load(FileInputStream(secretsPropertiesFile))
    }

    val tmdbPropertiesFile = project.rootProject.file("./tmdb.properties")
    val tmdbProperties = Properties()

    if (tmdbPropertiesFile.exists()) {
        tmdbProperties.load(tmdbPropertiesFile.inputStream())
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
        buildFeatures.buildConfig = true

        buildConfigField("String", "TMDB_KEY", "\"${tmdbProperties["tmdb_key"]}\"")
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${secretProperties["web_client_id"]}\""
        )
    }
}
