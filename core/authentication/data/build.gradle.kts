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
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.kmp)

            // Koin
            api(libs.koin.core)

            // Firebase - FireStore
            implementation(libs.firebase.kmp.auth)
            implementation(libs.firebase.kmp.firestore)

            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.NETWORK))
        }

        androidMain.dependencies {
            // Credentials Manager
            implementation(libs.credentials)
            implementation(libs.credentials.play.services)
            implementation(libs.google.identity)

            // Facebook
            implementation(libs.facebook.android.sdk)
            implementation(libs.facebook.login)

            // Google
            implementation(libs.play.services.auth)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.data"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
