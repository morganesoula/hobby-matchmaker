package com.msoula.convention

import ProjectApplication
import ProjectConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.kotlin.dsl.get

internal fun ApplicationExtension.configureAndroidApplication() {
    compileOptions {
        sourceCompatibility = ProjectConfig.PROJECT_CONFIG_JAVA_VERSION
        targetCompatibility = ProjectConfig.PROJECT_CONFIG_JAVA_VERSION
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = ProjectApplication.PROJECT_APPLICATION_ID
        minSdk = ProjectConfig.PROJECT_CONFIG_MIN_SDK_VERSION
        targetSdk = ProjectConfig.PROJECT_CONFIG_TARGET_SDK_VERSION
        versionCode = ProjectApplication.PROJECT_APPLICATION_VERSION_CODE
        versionName = ProjectApplication.PROJECT_APPLICATION_VERSION_NAME
        compileSdk = ProjectConfig.PROJECT_CONFIG_SDK_VERSION
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isJniDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        getByName("release") {
            isMinifyEnabled = false
            isJniDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures.buildConfig = true
}

internal fun CommonExtension<*, *, *, *, *, *>.configureMultiplatformAndroid() {
    compileSdk = ProjectConfig.PROJECT_CONFIG_SDK_VERSION

    compileOptions {
        sourceCompatibility = ProjectConfig.PROJECT_CONFIG_JAVA_VERSION
        targetCompatibility = ProjectConfig.PROJECT_CONFIG_JAVA_VERSION
    }

    defaultConfig {
        minSdk = ProjectConfig.PROJECT_CONFIG_MIN_SDK_VERSION
    }
}
