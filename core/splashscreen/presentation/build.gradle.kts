plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.splashscreen.presentation"
}
