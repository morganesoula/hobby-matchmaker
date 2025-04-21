package com.msoula.convention

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureMultiplatformIos() {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
