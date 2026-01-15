plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.kover)
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.features.movies.domain"
    }

    sourceSets {
        commonMain {
            dependencies {
                // Modules
                implementation(project(Modules.AUTHENTICATION_DOMAIN))
                implementation(project(Modules.COMMON))
            }
        }
    }
}
