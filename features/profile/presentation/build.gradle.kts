plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.kover)
}

multiplatformConfig {
    useCoil()
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.features.profile.presentation"
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
        }

        commonMain.dependencies {
            // Images
            implementation(libs.findLibrary("file-kit-core").get())
            implementation(libs.findLibrary("file-kit-compose").get())
            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.PROFILE_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.SOCIAL_PRESENTATION))
        }
    }
}
