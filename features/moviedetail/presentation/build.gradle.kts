@file:OptIn(ExperimentalSpmForKmpFeature::class)

import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.github.frankois944.spmForKmp.utils.ExperimentalSpmForKmpFeature

plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.spm.kmp)
}

multiplatformConfig {
    useCoil()
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.swiftPackageConfig("nativeIosPlayerShared") {
            minIos = "15.0"
            dependency {
                remotePackageVersion(
                    url = uri("https://github.com/youtube/youtube-ios-player-helper.git"),
                    products = {
                        add("YouTubeiOSPlayerHelper")
                    },
                    version = "1.0.4"
                )
            }
        }
    }

    android {
        namespace = "com.msoula.hobbymatchmaker.features.moviedetail.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            // Coil
            implementation(libs.findLibrary("coil-network").get())

            // Media Player
            implementation(libs.findLibrary("media-player-kmp").get())

            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.SOCIAL_DOMAIN))

        }

        androidMain.dependencies {
            // Network for Coil
            implementation(libs.findLibrary("ktor-client-android").get())

            // Media player
            implementation(libs.findLibrary("youtube-player").get())
        }

        iosMain.dependencies {
            // Network for Coil
            implementation(libs.findLibrary("ktor-client-darwin").get())
        }
    }
}
