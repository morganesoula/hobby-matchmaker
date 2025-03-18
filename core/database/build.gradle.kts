import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.room.multiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    metadata {
        compilations.all {
            val compilationName = name
            compileTaskProvider.configure {
                if (this is KotlinCompileCommon) {
                    moduleName = "${project.group}:${project.name}_$compilationName"
                }
            }
        }
    }

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
            kotlin.srcDir("build/generated/ksp/metadata")

            dependencies {
                // Koin
                api(libs.koin.core)

                // Modules
                implementation(project(Modules.DAO))

                // Room
                implementation(libs.room.runtime)
                implementation(libs.sqlite)
                implementation(libs.sqlite.bundled)
            }
        }

        androidMain.dependencies {
            // Koin
            implementation(libs.koin.android)

            // Room
            implementation(libs.room.runtime.android)
        }
    }
}

room { schemaDirectory("$projectDir/schemas") }

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

dependencies {
    add("kspCommonMainMetadata", libs.room.compiler)
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}
