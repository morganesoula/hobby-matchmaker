plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.features.profile.domain"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.SESSION_DOMAIN))
        }
    }
}
