import extensions.appModuleDeps
import extensions.instrumentationTestDeps
import extensions.unitTestDeps

plugins {
    id(Plugins.ANDROID_APPLICATION)
    kotlin(Plugins.ANDROID)
    kotlin(Plugins.KAPT)
    id(Plugins.DAGGER_HILT)
    id(Plugins.GOOGLE_SERVICES)
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
        jvmTarget = JavaVersion.VERSION_17.toString()
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
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources.excludes.add("**/*")
    }

    kotlin {
        jvmToolchain(17)
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
