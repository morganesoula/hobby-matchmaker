import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.serialization)
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
                // FireStore
                implementation(libs.firebase.kmp.firestore)

                // Koin
                api(libs.koin.core)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.negotiation)
                implementation(libs.ktor.serialization.json)

                // Modules
                implementation(project(Modules.NETWORK))
                implementation(project(Modules.DATABASE))
                implementation(project(Modules.DAO))
                implementation(project(Modules.MOVIE_DOMAIN))
                implementation(project(Modules.COMMON))

                // Room
                implementation(libs.room.runtime)
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.data"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }

    lint {
        disable += listOf("CoroutineCreationDuringComposition")
    }
}
