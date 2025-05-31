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
    }

    sourceSets.jvmTest.dependencies {
        implementation(libs.findLibrary("kotest-runner-junit5").get())
    }
}
