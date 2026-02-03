plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.core.user.domain"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
        }
    }
}
