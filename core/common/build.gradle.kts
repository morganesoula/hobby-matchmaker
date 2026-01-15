plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.core.common"

        androidResources {
            ignoreAssetsPattern = ""
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Resources
            implementation(libs.findLibrary("compose-runtime").get())
            implementation(libs.findLibrary("compose-resources").get())

            implementation(libs.findLibrary("kotlinx-datetime").get())
            implementation(libs.findLibrary("kotlinx-io").get())
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("activity-compose").get())
            implementation(libs.findLibrary("facebook-android-sdk").get())
            implementation(libs.findLibrary("timber-android").get())
        }
    }
}
