import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id(Plugins.DAGGER_HILT)
    id(Plugins.GOOGLE_SERVICES)
    kotlin(Plugins.KAPT)
    id("kotlin-parcelize")
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
            BuildConfigField("String", getFacebookKey(), "get facebook token"),
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

    bundle {
        language {
            enableSplit = false
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        release {
            isMinifyEnabled = true
            isDebuggable = false
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
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }
}

kapt {
    arguments {
        arg("dagger.hilt.shareTestComponents", "true")
    }
}

dependencies {
    lintChecks(libs.compose.lint.checks)

    // Compose
    implementation(libs.ui.tooling)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.activity.compose)
    implementation(libs.runtime)
    implementation(libs.lifecycle.viewmodel.compose)

    // AndroidX
    implementation(libs.core.ktx)
    api(libs.appcompat)
    implementation(libs.activity.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.collections.immutable)

    // Facebook
    implementation(libs.facebook.android.sdk)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)
    implementation(libs.firebase.ui.auth)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Modules
    implementation(project(Modules.AUTHENTICATION_DATA))
    implementation(project(Modules.AUTHENTICATION_DOMAIN))
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DATABASE))
    implementation(project(Modules.DAO))
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.DI))
    implementation(project(Modules.LOGIN_PRESENTATION))
    implementation(project(Modules.MOVIE_DATA))
    implementation(project(Modules.MOVIE_DOMAIN))
    implementation(project(Modules.MOVIE_PRESENTATION))
    implementation(project(Modules.NAVIGATION))
    implementation(project(Modules.NETWORK))
    implementation(project(Modules.SESSION_DOMAIN))
    implementation(project(Modules.SPLASHSCREEN_PRESENTATION))

    // Navigation
    implementation(libs.navigation.ui.ktx)

    // Unit Test
    testImplementation(libs.assertk)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.test.core.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
    testImplementation(libs.truth)

    // Instrumentation Test
    androidTestImplementation(libs.junit.ktx)
    androidTestImplementation(libs.core.testing)
    androidTestImplementation(libs.navigation.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.truth)
}
