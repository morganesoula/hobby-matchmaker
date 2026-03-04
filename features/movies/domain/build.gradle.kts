plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
}

kotlin {
    android {
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
