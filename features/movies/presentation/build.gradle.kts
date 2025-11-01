plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.kover)
}

multiplatformConfig {
    useCoil()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Coil
            implementation(libs.findLibrary("coil-network").get())

            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.NETWORK))
        }

        androidMain.dependencies {
            // Network for Coil
            implementation(libs.findLibrary("ktor-client-android").get())
        }

        iosMain.dependencies {
            // Network for Coil
            implementation(libs.findLibrary("ktor-client-darwin").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.presentation"
}
