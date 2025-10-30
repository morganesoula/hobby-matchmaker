plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Compose Resources
            implementation(compose.runtime)
            implementation(compose.components.resources)

            implementation(libs.findLibrary("kotlinx-datetime").get())
            implementation(libs.findLibrary("kotlinx-io").get())

            // FireStore
            implementation(libs.findLibrary("firebase-kmp-firestore").get())
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("activity-compose").get())
            implementation(libs.findLibrary("facebook-android-sdk").get())
            implementation(libs.findLibrary("timber-android").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.common"
    androidResources {
        enable = false
    }
}
