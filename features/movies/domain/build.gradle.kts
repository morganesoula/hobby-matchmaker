import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-multiplatform`
    `android-library`
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
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.kmp)
                
                // Koin
                implementation(libs.koin.core)

                // Modules
                implementation(project(Modules.COMMON))
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.domain"
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
    namespace = "com.msoula.hobbymatchmaker.features.movies.domain"
}

dependencies {
    // Arrow
    implementation(libs.arrow.core)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.COMMON))
} */
