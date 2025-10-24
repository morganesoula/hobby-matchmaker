plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Module
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.LOGIN_PRESENTATION))
            implementation(project(Modules.MOVIE_PRESENTATION))
            implementation(project(Modules.MOVIE_DETAIL_PRESENTATION))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.SPLASHSCREEN_PRESENTATION))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation.presentation"
}
