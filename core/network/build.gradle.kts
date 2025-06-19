import java.util.Properties

plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    id("io.github.frankois944.spmForKmp") version "0.11.2"
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.findBundle("ktor").get())
            implementation(libs.findLibrary("ktor-client-cio").get())

            // Modules
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.COMMON))
        }

        androidMain.dependencies {
            // Google
            implementation(libs.findLibrary("play-services-auth").get())
        }

        iosMain.dependencies {
            // Ktor client
            implementation(libs.findLibrary("ktor-client-darwin").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.network"
    buildFeatures.buildConfig = true

    val tmdbPropertiesFile = project.rootProject.file("./secrets.properties")
    val tmdbProperties = Properties()

    if (tmdbPropertiesFile.exists()) {
        tmdbProperties.load(tmdbPropertiesFile.inputStream())
    }

    defaultConfig {
        buildConfigField("String", "TMDB_KEY", "\"${tmdbProperties["tmdb_key"]}\"")
    }
}

swiftPackageConfig {
    create("network") {}
}
