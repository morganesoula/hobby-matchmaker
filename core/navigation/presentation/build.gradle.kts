plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

multiplatformConfig {
    useDecomposeWithCompose()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Module
            implementation(project(Modules.COMMON))
            implementation(project(Modules.LOGIN_PRESENTATION))
            implementation(project(Modules.MOVIE_PRESENTATION))
            implementation(project(Modules.MOVIE_DETAIL_PRESENTATION))
            implementation(project(Modules.NAVIGATION_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.SPLASHSCREEN_PRESENTATION))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation.presentation"
}
