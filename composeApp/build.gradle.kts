import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.application)
}

multiplatformConfig {
    useCoil()
    useDecomposeWithCompose()
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Logger
            implementation(libs.findLibrary("napier").get())

            // Modules
            implementation(project(Modules.AUTHENTICATION_DATA))
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.DI))
            implementation(project(Modules.LOGIN_DOMAIN))
            implementation(project(Modules.LOGIN_PRESENTATION))
            implementation(project(Modules.MOVIE_DATA))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.MOVIE_PRESENTATION))
            implementation(project(Modules.MOVIE_DETAIL_DATA))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.MOVIE_DETAIL_PRESENTATION))
            implementation(project(Modules.NAVIGATION_DOMAIN))
            implementation(project(Modules.NAVIGATION_PRESENTATION))
            implementation(project(Modules.NETWORK))
            implementation(project(Modules.SESSION_DATA))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.SPLASHSCREEN_PRESENTATION))
        }


        androidMain.dependencies {
            // AndroidX
            api(libs.findLibrary("appcompat").get())
            implementation(libs.findLibrary("activity-compose").get())

            // Facebook
            implementation(libs.findLibrary("facebook-android-sdk").get())

            // Firebase to make :generateDebugAndroidTestLintModel pass
            implementation("com.google.firebase:firebase-auth-ktx:23.2.0")
            implementation("com.google.firebase:firebase-common-ktx:21.0.0")
            implementation("com.google.firebase:firebase-firestore:25.1.2")

            // Koin
            implementation(libs.findLibrary("koin-android").get())

            // Timber
            implementation(libs.findLibrary("timber-android").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker"

    val secretsPropertiesFile = rootProject.file("secrets.properties")
    val secretProperties = Properties()

    if (secretsPropertiesFile.exists()) {
        secretProperties.load(FileInputStream(secretsPropertiesFile))
    }

    defaultConfig {
        manifestPlaceholders["facebookApplicationID"] =
            secretProperties["facebook_application_id"] ?: ""
        manifestPlaceholders["facebookClientToken"] =
            secretProperties["facebook_client_token"] ?: ""

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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker"
    generateResClass = always
}

/* plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
}

val secretsPropertiesFile = rootProject.file("secrets.properties")
val secretProperties = Properties()

if (secretsPropertiesFile.exists()) {
    secretProperties.load(FileInputStream(secretsPropertiesFile))
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

        manifestPlaceholders["facebookApplicationID"] =
            secretProperties["facebook_application_id"] ?: ""
        manifestPlaceholders["facebookClientToken"] =
            secretProperties["facebook_client_token"] ?: ""

        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
        vectorDrawables.useSupportLibrary = true

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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

    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
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

dependencies {
    lintChecks(libs.compose.lint.checks)

    // Compose
    implementation(libs.ui.tooling)
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

    // Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.firestore)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.serialization.json)

    // com.msoula.convention.Modules
    implementation(project(com.msoula.convention.Modules.AUTHENTICATION_DATA))
    implementation(project(com.msoula.convention.Modules.AUTHENTICATION_DOMAIN))
    implementation(project(com.msoula.convention.Modules.COMMON))
    implementation(project(com.msoula.convention.Modules.DATABASE))
    implementation(project(com.msoula.convention.Modules.DESIGN))
    implementation(project(com.msoula.convention.Modules.DI))
    implementation(project(com.msoula.convention.Modules.LOGIN_DOMAIN))
    implementation(project(com.msoula.convention.Modules.LOGIN_PRESENTATION))
    implementation(project(com.msoula.convention.Modules.MOVIE_DATA))
    implementation(project(com.msoula.convention.Modules.MOVIE_DOMAIN))
    implementation(project(com.msoula.convention.Modules.MOVIE_PRESENTATION))
    implementation(project(com.msoula.convention.Modules.MOVIE_DETAIL_DATA))
    implementation(project(com.msoula.convention.Modules.MOVIE_DETAIL_DOMAIN))
    implementation(project(com.msoula.convention.Modules.MOVIE_DETAIL_PRESENTATION))
    implementation(project(com.msoula.convention.Modules.NAVIGATION))
    implementation(project(com.msoula.convention.Modules.NETWORK))
    implementation(project(com.msoula.convention.Modules.SHARED))
    implementation(project(com.msoula.convention.Modules.SESSION_DATA))
    implementation(project(com.msoula.convention.Modules.SESSION_DOMAIN))
    implementation(project(com.msoula.convention.Modules.SPLASHSCREEN_PRESENTATION))

    // Navigation
    implementation(libs.compose.navigation)

    // Unit Test
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.ktx)
    testImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
} */
