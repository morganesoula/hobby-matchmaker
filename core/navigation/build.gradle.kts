import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "2.1.0"
    `android-library`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.kotlinx.serialization)

            // Modules
            implementation(project(Modules.DESIGN))

            // Navigation
            implementation(libs.voyager.navigator)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
/* plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation"
}

dependencies {
    // Compose
    implementation(libs.activity.compose)
    implementation(libs.runtime)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.DESIGN))

    // Navigation
    implementation(libs.compose.navigation)

    // Serialization
    implementation(libs.kotlinx.serialization)
} */
