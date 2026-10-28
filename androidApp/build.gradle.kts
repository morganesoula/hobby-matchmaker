import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.msoula.hobbymatchmaker"
    compileSdk = 37
    buildFeatures.buildConfig = true

    val secretsPropertiesFile = rootProject.file("secrets.properties")
    val secretProperties = Properties().apply {
        if (secretsPropertiesFile.exists()) load(FileInputStream(secretsPropertiesFile))
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties().apply {
        if (keystorePropertiesFile.exists()) load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            val storeFilePath = keystoreProperties.getProperty("storeFile").orEmpty()
            if (storeFilePath.isNotBlank()) {
                storeFile = file(storeFilePath)
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    defaultConfig {
        applicationId = "com.msoula.hobbymatchmaker"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.0"

        manifestPlaceholders["facebookApplicationID"] = secretProperties["facebook_application_id"] ?: ""
        manifestPlaceholders["facebookClientToken"] = secretProperties["facebook_client_token"] ?: ""

        buildConfigField(
            "String",
            "KOTZILLA_KEY",
            "\"${secretProperties["kotzilla_key"]}\""
        )
        buildConfigField(
            "String",
            "FIREBASE_APP_ID",
            "\"${secretProperties["firebase_application_id"]}\""
        )
        buildConfigField(
            "String",
            "FIREBASE_API_KEY",
            "\"${secretProperties["firebase_api_key"]}\""
        )
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${secretProperties["web_client_id"]}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xskip-prerelease-check")
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.appcompat)
    implementation(libs.activity.compose)

    // Facebook
    implementation(libs.facebook.android.sdk)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.common)
    implementation(libs.firebase.auth)

    // Google
    implementation(libs.play.services.auth)
    implementation(libs.credentials.play.services)
    implementation(libs.google.identity)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(":core:authentication:domain"))
    implementation(project(":core:login:presentation"))
    implementation(project(":core:navigation:presentation"))

    // Timber
    implementation(libs.timber.android)
}
