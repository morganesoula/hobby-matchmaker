plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.kover)
}

multiplatformConfig {
    useCoil()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.PROFILE_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.profile.presentation"
}

