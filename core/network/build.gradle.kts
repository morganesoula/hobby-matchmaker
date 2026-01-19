import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.spm.kmp)
    alias(libs.plugins.build.konfig)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.core.network"
    }

    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.findBundle("ktor").get())

            // Modules
            implementation(project(Modules.COMMON))
        }

        androidMain.dependencies {
            // Google
            implementation(libs.findLibrary("play-services-auth").get())

            // Ktor client
            implementation(libs.findLibrary("ktor-client-cio").get())
        }

        iosMain.dependencies {
            // Ktor client
            implementation(libs.findLibrary("ktor-client-darwin").get())
        }
    }
}

buildkonfig {
    packageName = "com.msoula.hobbymatchmaker.core.network"

    defaultConfigs {
        val tmdbPropertiesFile = project.rootProject.file("./secrets.properties")
        val tmdbProperties = Properties()

        if (tmdbPropertiesFile.exists()) {
            tmdbProperties.load(tmdbPropertiesFile.inputStream())
        }

        buildConfigField(STRING, "TMDB_KEY", tmdbProperties["tmdb_key"]?.toString() ?: "")
    }
}

swiftPackageConfig {
    create("network") {}
}
