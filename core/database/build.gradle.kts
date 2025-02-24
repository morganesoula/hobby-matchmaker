import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.ksp)
    alias(libs.plugins.room.multiplatform)
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
            // Koin
            implementation(libs.koin.core)

            // Modules
            implementation(project(Modules.DAO))

            // Room
            implementation(libs.room.runtime)
        }

        androidMain.dependencies {
            implementation(libs.room.runtime)

            // Koin
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.room.runtime)
        }
    }
}

room { schemaDirectory("$projectDir/schemas") }

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)

    add("kspAndroid", libs.koin.compiler)
    add("kspIosSimulatorArm64", libs.koin.compiler)
    add("kspIosX64", libs.koin.compiler)
    add("kspIosSimulatorArm64", libs.koin.compiler)
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.database"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}


