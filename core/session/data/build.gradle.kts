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
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.kmp)

            // DataStore
            implementation(libs.datastore.preferences)

            // Firebase - FireStore
            implementation(libs.firebase.kmp.auth)
            implementation(libs.firebase.kmp.firestore)

            // Koin
            api(libs.koin.core)

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.SESSION_DOMAIN))
        }

        androidMain.dependencies {
            // Koin
            implementation(libs.koin.android)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.session.data"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
