import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import io.github.frankois944.spmForKmp.swiftPackageConfig
import java.util.Properties

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.spm.kmp)
    alias(libs.plugins.build.konfig)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.core.common"

        androidResources {
            ignoreAssetsPattern = ""
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.swiftPackageConfig("common") {
            minIos = "15.0"
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Resources
            implementation(libs.findLibrary("compose-runtime").get())
            implementation(libs.findLibrary("compose-resources").get())

            // DataStore
            implementation(libs.findLibrary("datastore-preferences").get())

            //Ktor
            implementation(libs.findBundle("ktor").get())

            // Kotlin
            implementation(libs.findLibrary("kotlinx-datetime").get())
            implementation(libs.findLibrary("kotlinx-io").get())
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("activity-compose").get())
            implementation(libs.findLibrary("facebook-android-sdk").get())
            implementation(libs.findLibrary("timber-android").get())
            implementation(libs.findLibrary("play-services-auth").get())
            implementation(libs.findLibrary("ktor-client-cio").get())
        }

        iosMain.dependencies {
            // Ktor client
            implementation(libs.findLibrary("ktor-client-darwin").get())
        }
    }
}

buildkonfig {
    packageName = "com.msoula.hobbymatchmaker.core.common"

    defaultConfigs {
        val tmdbPropertiesFile = project.rootProject.file("./secrets.properties")
        val tmdbProperties = Properties()

        if (tmdbPropertiesFile.exists()) {
            tmdbProperties.load(tmdbPropertiesFile.inputStream())
        }

        buildConfigField(STRING, "APP_SECRET", tmdbProperties["app_secret"]?.toString() ?: "")
    }
}
