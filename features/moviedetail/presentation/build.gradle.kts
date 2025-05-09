plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    id("io.github.frankois944.spmForKmp") version "0.8.2"
}

multiplatformConfig {
    useDecomposeWithCompose()
    useCoil()
}

kotlin {
    iosArm64().apply {
        compilations["main"].cinterops.create("nativeIosPlayerShared")
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.NETWORK))
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("youtube-player").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.presentation"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.features.moviedetail.presentation"
    generateResClass = always
}

swiftPackageConfig {
    create("nativeIosPlayerShared") {
        minIos = "18.0"
    }
}
