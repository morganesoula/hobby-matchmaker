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

            // FireStore
            implementation(libs.findLibrary("firebase-kmp-firestore").get())

            // Ktor
            implementation(libs.findBundle("ktor").get())

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
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.common"
    androidResources {
        enable = false
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.common"
    generateResClass = always
}
