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

                // Firebase
                implementation(libs.firebase.kmp.auth)

                // Koin
                implementation(libs.koin.core)

                // Modules
                implementation(project(Modules.DI))
                implementation(project(Modules.COMMON))
                implementation(project(Modules.SESSION_DOMAIN))
            }
        }

        androidMain.dependencies {
            // Facebook
            implementation(libs.facebook.android.sdk)
            implementation(libs.facebook.login)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.domain"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
