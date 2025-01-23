import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
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

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.kotlinx.coroutines.kmp)

            // Firebase/FireStore - via GitLive https://github.com/GitLiveApp/firebase-kotlin-sdk
            implementation(libs.firebase.kmp.auth)
            implementation(libs.firebase.kmp.firestore)

            // Koin
            implementation(libs.koin.core)

            // Modules
            implementation(project(Modules.DESIGN))
        }

        androidMain.dependencies {
            implementation(libs.activity.compose)

            // Facebook
            implementation(libs.facebook.android.sdk)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.common"
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
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.common"
}

dependencies {
    // Compose
    implementation(libs.activity.compose)
    implementation(libs.runtime)

    // Facebook
    implementation(libs.facebook.android.sdk)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.firestore)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.DESIGN))
} */
