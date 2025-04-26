package com.msoula.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureMultiplatformIos() {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}

internal fun KotlinMultiplatformExtension.configureIOSApplication() {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += listOf(
                "-Xdisable-phases=VerifyBitcode"
            )
        }
    }
}

fun KotlinMultiplatformExtension.configureCInterops(project: Project) {
    if (project.name != "network") return

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.compilations.getByName("main").cinterops.create("network")
    }
}
