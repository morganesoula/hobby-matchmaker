import com.android.build.api.variant.BuildConfigField
import extensions.appModuleDeps
import extensions.instrumentationTestDeps
import extensions.unitTestDeps
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id(Plugins.DAGGER_HILT)
    id(Plugins.GOOGLE_SERVICES)
    kotlin(Plugins.KAPT)
}

fun getFacebookKey(): String {
    val propFile = rootProject.file("./facebook.properties")

    if (propFile.exists()) {
        val properties = Properties()
        properties.load(FileInputStream(propFile))
        return properties.getProperty("facebook_client_token")
    } else {
        throw FileNotFoundException()
    }
}

androidComponents {
    onVariants {
        it.buildConfigFields.put(
            "facebook_client_token",
            BuildConfigField("String", getFacebookKey(), "get facebook token")
        )
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker"
    compileSdk = AndroidConfig.COMPILE_SDK

    defaultConfig {
        applicationId = AndroidConfig.APPLICATION_ID
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME

        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
        getByName("androidTest").java.srcDir("src/androidTest/kotlin")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    packaging {
        resources.excludes.add("**/*")
    }
}

kapt {
    arguments {
        arg("dagger.hilt.shareTestComponents", "true")
    }
}

dependencies {
    lintChecks("com.slack.lint.compose:compose-lint-checks:1.3.1")

    appModuleDeps()
    unitTestDeps()
    instrumentationTestDeps()
}
