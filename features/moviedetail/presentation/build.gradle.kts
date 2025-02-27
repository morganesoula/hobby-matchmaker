import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
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
            implementation(libs.kotlinx.coroutines.kmp)

            // Coil
            implementation(libs.coil.compose)

            // Compose
            implementation(compose.runtime)
            implementation(compose.components.resources)
            implementation(compose.material3)

            // Koin
            implementation(libs.koin.core)

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))

            // Voyager
            implementation(libs.voyager.screen.model)
        }

        androidMain.dependencies {
            // Youtube
            implementation(libs.youtube.player)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.presentation"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.features.moviedetail.presentation"
    generateResClass = always
}
