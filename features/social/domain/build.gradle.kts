plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.features.social.domain"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
        }
    }
}
