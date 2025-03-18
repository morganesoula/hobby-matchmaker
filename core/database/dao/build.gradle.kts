import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.room.multiplatform)
    alias(libs.plugins.ksp)
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
            implementation(compose.runtime)
            implementation(libs.kotlinx.coroutines.kmp)
            implementation(libs.kotlinx.serialization)

            //
            implementation(libs.room.common)
            implementation(libs.room.runtime)
        }

        androidMain.dependencies {
            // Room
            implementation(libs.room.runtime.android)
        }
    }
}

room { schemaDirectory("$projectDir/schemas") }

android {
    namespace = "com.msoula.hobbymatchmaker.core.database.dao"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.room.compiler)
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}
