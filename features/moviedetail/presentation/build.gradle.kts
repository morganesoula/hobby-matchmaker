import java.net.URI

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
            // Media Player
            implementation(libs.findLibrary("media-player-kmp").get())

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.NETWORK))

        }

        androidMain.dependencies {
            // Back handler
            implementation(libs.findLibrary("activity-compose").get())

            // Media player
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

        dependency {
            remotePackageVersion(
                url = URI("https://github.com/youtube/youtube-ios-player-helper.git"),
                products = {
                    add("YouTubeiOSPlayerHelper", exportToKotlin = true)
                },
                version = "1.0.4"
            )
        }
    }
}
