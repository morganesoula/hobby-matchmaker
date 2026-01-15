package com.msoula.convention

import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureMultiplatform(
    libs: VersionCatalog
) {
    sourceSets.commonMain.dependencies {
        // Coroutines
        implementation(libs.findLibrary("kotlinx-coroutines-kmp").get())

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
    sourceSets.commonMain.dependencies {
        // Koin
        api(libs.findLibrary("koin-core").get())
    }
}