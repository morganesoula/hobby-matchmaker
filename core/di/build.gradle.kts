import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/* plugins {
    kotlin("multiplatform")
    `android-library`
    //`kotlin-android`
    alias(libs.plugins.compose.compiler)
}

//apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.di"
}

dependencies {
    // Core
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.runtime)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.DESIGN))
} */

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
            // Core
            implementation(libs.core.ktx)
            implementation(compose.runtime)

            // Koin
            implementation(libs.koin.core)
            //implementation(libs.koin.android)

            // Modules
            implementation(project(Modules.DESIGN))
        }

    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.di"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
