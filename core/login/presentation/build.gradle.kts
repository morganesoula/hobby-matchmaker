import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    id("io.github.frankois944.spmForKmp") version "0.8.2"
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.addAll("-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.msoula.hobbymatchmaker.core.login.presentation.models.HMMParcelize")
        }
    }

    iosArm64().apply {
        compilations["main"].cinterops.create("nativeIosAuthShared")
    }

    /* listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.compilations {
            val main by getting {
                cinterops.create("nativeIosShared")
            }
        }
    } */

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DATA))
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.DI))
            implementation(project(Modules.LOGIN_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("saved-state-kmp").get())

            // Compose
            implementation(libs.findLibrary("activity-compose").get())

            // Credentials Manager
            implementation(libs.findLibrary("credentials").get())
            implementation(libs.findLibrary("credentials-play-services").get())
            implementation(libs.findLibrary("google-identity").get())

            // Facebook
            implementation(libs.findLibrary("facebook-android-sdk").get())

            // Google
            implementation(libs.findLibrary("play-services-auth").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.login.presentation"
    buildFeatures.buildConfig = true

    defaultConfig {
        val secretsPropertiesFile = project.rootProject.file("secrets.properties")
        val secretProperties = Properties()

        if (secretsPropertiesFile.exists()) {
            secretProperties.load(FileInputStream(secretsPropertiesFile))
        }

        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${secretProperties["web_client_id"]}\""
        )
    }

    lint {
        disable += listOf("CoroutineCreationDuringComposition")
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.login.presentation"
    generateResClass = always
}

swiftPackageConfig {
    create("nativeIosAuthShared") {
        minIos = "18.0"
    }
}
