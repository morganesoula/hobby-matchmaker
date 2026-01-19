plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.features.moviedetail.domain"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
        }
    }
}
