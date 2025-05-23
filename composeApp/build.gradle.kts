import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.io.FileInputStream
import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.application)
    id("io.github.frankois944.spmForKmp") version "0.8.2"
}

multiplatformConfig {
    useCoil()
    useDecomposeWithCompose()
    useFirebase()
}

kotlin {
    val xcf = XCFramework()

    iosArm64().apply {
        compilations["main"].cinterops.create("nativeIosShared")
        binaries.framework {
            baseName = "ComposeApp"
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
                version = "11.12.0"
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
