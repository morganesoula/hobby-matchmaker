plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.features.hub.domain"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.SOCIAL_DOMAIN))
        }
    }
}
