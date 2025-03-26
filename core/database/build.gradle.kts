import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.sqldelight)
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

    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // Koin
            api(libs.koin.core)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }


        androidMain.dependencies {
            // Koin
            implementation(libs.koin.android)

            // SQLDelight
            implementation(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
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

sqldelight {
    databases {
        create("HMMDatabase") {
            packageName = "com.msoula.hobbymatchmaker.core.database"
        }
    }
}
