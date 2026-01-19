plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.features.social.presentation"
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
        }

        commonMain.dependencies {
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.SOCIAL_DOMAIN))
        }
    }
}
