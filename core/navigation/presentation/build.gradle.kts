import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.material3)

            // Decompose
            implementation(libs.decompose.core)
            implementation(libs.decompose.compose)

            // Module
            implementation(project(Modules.COMMON))
            implementation(project(Modules.NAVIGATION_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation.presentation"
    compileSdk = AndroidConfig.COMPILE_SDK
    buildFeatures.buildConfig = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
