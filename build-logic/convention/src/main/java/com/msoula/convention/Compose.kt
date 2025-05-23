package com.msoula.convention

import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureCompose(
    libs: VersionCatalog,
    config: MultiplatformConfigExtension,
    compose: ComposePlugin.Dependencies
) {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets.commonMain.dependencies {
        // Coroutines
        implementation(libs.findLibrary("kotlinx-coroutines-kmp").get())
        implementation(libs.findLibrary("kotlinx-collections-immutable").get())

        // Coil
        if (config.useCoil) {
            implementation(libs.findLibrary("coil-compose").get())
        }

        // Compose
        implementation(compose.runtime)
        implementation(compose.components.resources)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)

        // Decompose - Compose
        if (config.useDecomposeWithCompose) {
            implementation(libs.findLibrary("decompose-core").get())
            implementation(libs.findLibrary("decompose-compose").get())
        }

        // Firebase - FireStore
        if (config.useFirebase) {
            implementation(libs.findLibrary("firebase-kmp-auth").get())
            implementation(libs.findLibrary("firebase-kmp-firestore").get())
        }

        // Koin
        api(libs.findLibrary("koin-core").get())
        implementation(libs.findLibrary("koin-compose").get())
        implementation(libs.findLibrary("koin-compose-viewmodel").get())
    }
}
