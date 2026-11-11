plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.features.settings.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.PROFILE_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
        }
    }
}
