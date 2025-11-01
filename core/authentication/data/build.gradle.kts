plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.kover)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            // Facebook
            implementation(libs.findLibrary("facebook-android-sdk").get())
            implementation(libs.findLibrary("facebook-login").get())
        }

        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.NETWORK))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.data"
}
