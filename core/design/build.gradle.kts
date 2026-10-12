import io.github.frankois944.spmForKmp.swiftPackageConfig
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
    android {
        namespace = "com.msoula.hobbymatchmaker.core.design"
        @Suppress("OPT_IN_USAGE")
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.swiftPackageConfig(cinteropName = "nativeIosPlayerShared") {
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

    sourceSets {
        commonMain.dependencies {
            // Coil
            implementation(libs.findLibrary("coil-network").get())

            // Modules
            implementation(project(Modules.COMMON))
        }

        androidMain.dependencies {
            // Media player
            implementation(libs.findLibrary("youtube-player").get())

            // Preview
            implementation(libs.findLibrary("compose-preview").get())
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.design"
    generateResClass = always
}
