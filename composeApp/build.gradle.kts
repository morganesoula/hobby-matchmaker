import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.io.FileInputStream
import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.application)
    alias(libs.plugins.kover)
    alias(libs.plugins.spm.kmp)
}

multiplatformConfig {
    useCoil()
    useFirebase()
}

kotlin {
    val xcf = XCFramework()

    iosArm64().apply {
        compilations["main"].cinterops.create("nativeIosShared")
        binaries.framework {
            baseName = "composeApp"
            isStatic = true
            xcf.add(this)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DATA))
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.LOGIN_DOMAIN))
            implementation(project(Modules.LOGIN_PRESENTATION))
            implementation(project(Modules.MOVIE_DATA))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.MOVIE_PRESENTATION))
            implementation(project(Modules.MOVIE_DETAIL_DATA))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.MOVIE_DETAIL_PRESENTATION))
            implementation(project(Modules.NAVIGATION_PRESENTATION))
            implementation(project(Modules.NETWORK))
            implementation(project(Modules.PROFILE_DATA))
            implementation(project(Modules.PROFILE_DOMAIN))
            implementation(project(Modules.PROFILE_PRESENTATION))
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

            // Google
            implementation(libs.findLibrary("play-services-auth").get())

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

kover {
    reports {

    }
}

dependencies {
    kover(project(Modules.AUTHENTICATION_DATA))
    kover(project(Modules.AUTHENTICATION_DOMAIN))
    kover(project(Modules.LOGIN_DOMAIN))
    kover(project(Modules.LOGIN_PRESENTATION))
    kover(project(Modules.MOVIE_DATA))
    kover(project(Modules.MOVIE_DOMAIN))
    kover(project(Modules.MOVIE_DETAIL_DATA))
    kover(project(Modules.MOVIE_DETAIL_DOMAIN))
    kover(project(Modules.MOVIE_DETAIL_PRESENTATION))
    kover(project(Modules.MOVIE_PRESENTATION))
    kover(project(Modules.PROFILE_DATA))
    kover(project(Modules.PROFILE_DOMAIN))
    kover(project(Modules.PROFILE_PRESENTATION))
    kover(project(Modules.SESSION_DATA))
}

swiftPackageConfig {
    create("nativeIosShared") {
        minIos = "18.0"

        dependency {
            remotePackageVersion(
                url = URI("https://github.com/firebase/firebase-ios-sdk.git"),
                products = {
                    add("FirebaseCore", exportToKotlin = true)
                    add("FirebaseAuth", exportToKotlin = true)
                    add("FirebaseFirestore", exportToKotlin = true)
                },
                version = "11.14.0"
            )
            remotePackageVersion(
                url = URI("https://github.com/google/GoogleSignIn-iOS"),
                products = {
                    add("GoogleSignIn", exportToKotlin = true)
                },
                version = "8.0.0"
            )
        }
    }
}
