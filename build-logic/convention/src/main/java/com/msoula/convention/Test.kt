package com.msoula.convention

import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureUnitTest(
    libs: VersionCatalog
) {
    sourceSets.commonTest.dependencies {
        // Kotest
        implementation(libs.findLibrary("kotest-framework-engine").get())
        implementation(libs.findLibrary("kotest-assertions-core").get())

        // Turbine
        implementation(libs.findLibrary("turbine").get())

        implementation(libs.findLibrary("kotlinx-coroutines-test").get())
    }
}
