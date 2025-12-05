import java.net.URI

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.spm.kmp)
}

multiplatformConfig {
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

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.NAVIGATION_PRESENTATION))
        }

        androidMain.dependencies {
            // Media player
            implementation(libs.findLibrary("youtube-player").get())

            // Preview
            implementation(compose.uiTooling)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.design"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.design"
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
