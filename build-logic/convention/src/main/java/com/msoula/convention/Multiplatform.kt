package com.msoula.convention

import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureMultiplatform(
    libs: VersionCatalog,
    config: MultiplatformConfigExtension
) {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets.commonMain.dependencies {
        // Coroutines
        implementation(libs.findLibrary("kotlinx-coroutines-kmp").get())

        // Firebase
        if (config.useFirebase) {
            implementation(libs.findLibrary("firebase-kmp-auth").get())
            implementation(libs.findLibrary("firebase-kmp-firestore").get())
        }

        // Koin
        api(libs.findLibrary("koin-core").get())

        // Kotzilla
        implementation(libs.findLibrary("kotzilla-sdk").get())

        // Navigation
        implementation(libs.findLibrary("compose-navigation").get())

        // Serialization
        implementation(libs.findLibrary("kotlinx-serialization").get())
    }
}

internal fun KotlinMultiplatformExtension.configureMultiplatformMinimalist(
    libs: VersionCatalog
) {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets.commonMain.dependencies {
        // Koin
        api(libs.findLibrary("koin-core").get())
    }
}


