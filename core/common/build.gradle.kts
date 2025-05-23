plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("kotlinx-datetime").get())

            // FireStore
            implementation(libs.findLibrary("firebase-kmp-firestore").get())

            // Modules
            implementation(project(Modules.DESIGN))
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("activity-compose").get())

            // Facebook
            implementation(libs.findLibrary("facebook-android-sdk").get())

            // Timber
            implementation(libs.findLibrary("timber-android").get())
        }

        iosMain.dependencies {  }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.common"
}
