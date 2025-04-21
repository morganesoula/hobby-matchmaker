import java.util.Properties

plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.findBundle("ktor").get())

            // Modules
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.COMMON))
        }

        androidMain.dependencies {
            // Google
            implementation(libs.findLibrary("play-services-auth").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.network"
    buildFeatures.buildConfig = true

    val tmdbPropertiesFile = project.rootProject.file("./tmdb.properties")
    val tmdbProperties = Properties()

    if (tmdbPropertiesFile.exists()) {
        tmdbProperties.load(tmdbPropertiesFile.inputStream())
    }

    defaultConfig {
        buildConfigField("String", "TMDB_KEY", "\"${tmdbProperties["tmdb_key"]}\"")
    }
}
