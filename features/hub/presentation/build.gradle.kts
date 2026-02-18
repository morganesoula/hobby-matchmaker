plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

multiplatformConfig {
    useCoil()
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.features.hub.presentation"
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
        }

        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.HUB_DOMAIN))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.NETWORK))
            implementation(project(Modules.SOCIAL_DOMAIN))
        }
    }
}
