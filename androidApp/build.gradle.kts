import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotzilla)
}

android {
    namespace = "com.msoula.hobbymatchmaker"
    compileSdk = 36
    buildFeatures.buildConfig = true

    val secretsPropertiesFile = rootProject.file("secrets.properties")
    val secretProperties = Properties().apply {
        if (secretsPropertiesFile.exists()) load(FileInputStream(secretsPropertiesFile))
    }

    defaultConfig {
        applicationId = "com.msoula.hobbymatchmaker"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    implementation(libs.facebook.android.sdk)
    implementation(libs.play.services.auth)
    implementation(libs.koin.android)
    implementation(libs.timber.android)

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

    // Kotzilla
    implementation(libs.kotzilla.sdk)

    // Modules
    implementation(project(":core:authentication:domain"))
    implementation(project(":core:login:presentation"))
    implementation(project(":core:navigation:presentation"))

    // Timber
    implementation(libs.timber.android)
}
