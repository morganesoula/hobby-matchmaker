@file:OptIn(ExperimentalSpmForKmpFeature::class)

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.github.frankois944.spmForKmp.utils.ExperimentalSpmForKmpFeature
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.spm.kmp)
    alias(libs.plugins.build.konfig)
}

multiplatformConfig {
    useCoil()
    useFirebase()
}

kotlin {
    extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
        namespace = "com.msoula.hobbymatchmaker.composeapp"
        compileSdk = ProjectConfig.PROJECT_CONFIG_SDK_VERSION
        minSdk = ProjectConfig.PROJECT_CONFIG_MIN_SDK_VERSION
    }

    val xcf = XCFramework()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "composeApp"
            isStatic = true
            xcf.add(this)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.swiftPackageConfig("nativeIosShared") {
            minIos = "15.0"

            dependency {
                remotePackageVersion(
                    url = uri("https://github.com/firebase/firebase-ios-sdk.git"),
                    products = {
                        add("FirebaseCore")
                        add("FirebaseAuth")
                        add("FirebaseFirestore")
                        add("FirebaseStorage")
                    },
                    version = "12.10.0"
                )
                remotePackageVersion(
                    url = uri("https://github.com/google/GoogleSignIn-iOS.git"),
                    products = {
                        add("GoogleSignIn")
                    },
                    version = "9.1.0"
                )
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Resources
            implementation(libs.findLibrary("compose-resources").get())

            // Modules
            implementation(project(Modules.AUTHENTICATION_DATA))
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.HUB_DOMAIN))
            implementation(project(Modules.HUB_PRESENTATION))
            implementation(project(Modules.LOGIN_DOMAIN))
            implementation(project(Modules.LOGIN_PRESENTATION))
            implementation(project(Modules.MOVIE_DATA))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.MOVIE_PRESENTATION))
            implementation(project(Modules.MOVIE_DETAIL_DATA))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.MOVIE_DETAIL_PRESENTATION))
            implementation(project(Modules.NAVIGATION_PRESENTATION))
            implementation(project(Modules.PROFILE_DATA))
            implementation(project(Modules.PROFILE_DOMAIN))
            implementation(project(Modules.PROFILE_PRESENTATION))
            implementation(project(Modules.SESSION_DATA))
            implementation(project(Modules.SESSION_DOMAIN))
            implementation(project(Modules.SOCIAL_DATA))
            implementation(project(Modules.SOCIAL_DOMAIN))
            implementation(project(Modules.SOCIAL_PRESENTATION))
            implementation(project(Modules.SPLASHSCREEN_PRESENTATION))
            implementation(project(Modules.USER_DATA))
            implementation(project(Modules.USER_DOMAIN))
        }
    }
}

buildkonfig {
    packageName = "com.msoula.hobbymatchmaker"
    exposeObjectWithName = "BuildKonfig"

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
