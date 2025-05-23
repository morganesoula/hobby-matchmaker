plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.splashscreen.presentation"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.splashscreen.presentation"
    generateResClass = always
}
