plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

multiplatformConfig {
    useCoil()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.DI))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.NETWORK))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.presentation"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.features.movies.presentation"
    generateResClass = always
}
