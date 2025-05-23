plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.NETWORK))
        }

        androidMain.dependencies {
            // Credentials Manager
            implementation(libs.findLibrary("credentials").get())
            implementation(libs.findLibrary("credentials-play-services").get())
            implementation(libs.findLibrary("google-identity").get())

            // Facebook
            implementation(libs.findLibrary("facebook-android-sdk").get())
            implementation(libs.findLibrary("facebook-login").get())

            // Google
            implementation(libs.findLibrary("play-services-auth").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.data"
}
