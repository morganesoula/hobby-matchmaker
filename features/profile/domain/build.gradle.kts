plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.SESSION_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.profile.domain"
}
