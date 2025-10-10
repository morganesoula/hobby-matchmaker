import java.net.URI

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.kover)
    alias(libs.plugins.spm.kmp)
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
            // Coil
            implementation(libs.findLibrary("coil-network").get())

            // Media Player
            implementation(libs.findLibrary("media-player-kmp").get())

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.NETWORK))

        }

        androidMain.dependencies {
            // Network for Coil
            implementation(libs.findLibrary("ktor-client-android").get())

            // Back handler
            implementation(libs.findLibrary("activity-compose").get())

            // Media player
            implementation(libs.findLibrary("youtube-player").get())
        }

        iosMain.dependencies {
            // Network for Coil
            implementation(libs.findLibrary("ktor-client-darwin").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.presentation"
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
