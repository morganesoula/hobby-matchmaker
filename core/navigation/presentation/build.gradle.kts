plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Module
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.LOGIN_PRESENTATION))
            implementation(project(Modules.MOVIE_PRESENTATION))
            implementation(project(Modules.MOVIE_DETAIL_PRESENTATION))
            implementation(project(Modules.PROFILE_PRESENTATION))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.SOCIAL_PRESENTATION))
            implementation(project(Modules.SPLASHSCREEN_PRESENTATION))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation.presentation"
}
