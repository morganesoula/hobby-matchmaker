plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.core.splashscreen.presentation"
        @Suppress("OPT_IN_USAGE")
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
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
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
        }
    }
}
