plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    androidLibrary {
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
