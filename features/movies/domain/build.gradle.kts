plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Modules
                implementation(project(Modules.COMMON))
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.domain"
}
