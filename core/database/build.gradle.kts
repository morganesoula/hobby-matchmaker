import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.minimalist)
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

    sourceSets {
        commonMain.dependencies {
            // SQLDelight
            implementation(libs.findLibrary("sqldelight-runtime").get())
            implementation(libs.findLibrary("sqldelight-coroutines").get())
        }

        androidMain.dependencies {
            // Koin
            implementation(libs.findLibrary("koin-android").get())

            // SQLDelight
            implementation(libs.findLibrary("sqldelight-android-driver").get())
        }

        iosMain.dependencies {
            implementation(libs.findLibrary("sqldelight-native-driver").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.database"
}

sqldelight {
    databases {
        create("HMMDatabase") {
            packageName = "com.msoula.hobbymatchmaker.core.database"
        }
    }
}
