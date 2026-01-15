import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.io.FileInputStream
import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.application)
    alias(libs.plugins.kover)
    alias(libs.plugins.spm.kmp)
    alias(libs.plugins.build.konfig)
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
        all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
        }

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
            implementation(project(Modules.SOCIAL_DATA))
            implementation(project(Modules.SOCIAL_DOMAIN))
            implementation(project(Modules.SOCIAL_PRESENTATION))
            implementation(project(Modules.SPLASHSCREEN_PRESENTATION))
        }

        androidMain.dependencies {
            // AndroidX
            api(libs.findLibrary("appcompat").get())
            implementation(libs.findLibrary("activity-compose").get())

            // Facebook
            implementation(libs.findLibrary("facebook-android-sdk").get())

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

    defaultConfig {
        val secretsPropertiesFile = rootProject.file("secrets.properties")
        val secretProperties = Properties()

        if (secretsPropertiesFile.exists()) {
            secretProperties.load(FileInputStream(secretsPropertiesFile))
        }

        manifestPlaceholders["facebookApplicationID"] =
            secretProperties["facebook_application_id"]?.toString() ?: ""
        manifestPlaceholders["facebookClientToken"] =
            secretProperties["facebook_client_token"]?.toString() ?: ""
    }
}

buildkonfig {
    packageName = "com.msoula.hobbymatchmaker"

    defaultConfigs {
        val secretsPropertiesFile = rootProject.file("secrets.properties")
        val secretProperties = Properties()

        if (secretsPropertiesFile.exists()) {
            secretProperties.load(FileInputStream(secretsPropertiesFile))
        }

        buildConfigField(
            STRING,
            "FIREBASE_APP_ID",
            secretProperties["firebase_application_id"]?.toString() ?: ""
        )
        buildConfigField(
            STRING,
            "FIREBASE_API_KEY",
            secretProperties["firebase_api_key"]?.toString() ?: ""
        )
        buildConfigField(
            STRING,
            "KOTZILLA_KEY",
            secretProperties["kotzilla_key"]?.toString() ?: ""
        )
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
                    // Don't export Firebase to Kotlin - using gitlive bindings instead
                    add("FirebaseCore", exportToKotlin = false)
                    add("FirebaseAuth", exportToKotlin = false)
                    add("FirebaseFirestore", exportToKotlin = false)
                },
                version = "11.14.0"
            )
            remotePackageVersion(
                url = URI("https://github.com/google/GoogleSignIn-iOS"),
                products = {
                    // Don't export GoogleSignIn to Kotlin - Swift code returns simple types
                    add("GoogleSignIn", exportToKotlin = false)
                },
                version = "8.0.0"
            )
        }
    }
}
