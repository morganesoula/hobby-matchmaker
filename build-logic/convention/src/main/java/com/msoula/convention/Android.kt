package com.msoula.convention

import ProjectConfig
import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureAndroidApplication() {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }
}

// Configure compiler options (can be in afterEvaluate)
internal fun KotlinMultiplatformExtension.configureMultiplatformAndroid() {
    androidLibrary {
        compileSdk = ProjectConfig.PROJECT_CONFIG_SDK_VERSION
        minSdk = ProjectConfig.PROJECT_CONFIG_MIN_SDK_VERSION

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
