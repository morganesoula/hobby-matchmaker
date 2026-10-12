package com.msoula.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureMultiplatformIos(project: Project) {
    // Check if iOS targets should be enabled (default: true for backward compatibility)
    val iosEnabled = project.findProperty("kmp.buildTargets.ios.enabled")?.toString()?.toBoolean() ?: true

    if (!iosEnabled) {
        project.logger.lifecycle("⏩ Skipping iOS targets for ${project.name} (disabled via kmp.buildTargets.ios.enabled)")
        return
    }

    iosArm64()
    iosSimulatorArm64()
}

internal fun KotlinMultiplatformExtension.configureIOSApplication() {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += listOf(
                "-Xdisable-phases=VerifyBitcode"
            )
        }
    }
}
