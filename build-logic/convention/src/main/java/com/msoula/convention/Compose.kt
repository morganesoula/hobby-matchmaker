package com.msoula.convention

import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureCompose(
    libs: VersionCatalog
) {
    sourceSets.commonMain.dependencies {
        // Coroutines
        implementation(libs.findLibrary("kotlinx-coroutines-kmp").get())
        implementation(libs.findLibrary("kotlinx-collections-immutable").get())

        // Compose
        implementation(libs.findLibrary("compose-runtime").get())
        implementation(libs.findLibrary("compose-resources").get())
        implementation(libs.findLibrary("compose-material3").get())
        //implementation(compose.materialIconsExtended)
        implementation(libs.findLibrary("compose-preview").get())

        // Koin
        api(libs.findLibrary("koin-core").get())
        implementation(libs.findLibrary("koin-compose").get())
        implementation(libs.findLibrary("koin-compose-viewmodel").get())

        // Kotzilla
        implementation(libs.findLibrary("kotzilla-sdk").get())

        // Navigation
        implementation(libs.findLibrary("compose-navigation").get())
    }
}
