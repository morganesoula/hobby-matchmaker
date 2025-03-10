import io.github.frankois944.spmForKmp.definition.SwiftDependency
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.net.URI
import java.util.Properties

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.serialization)
    id("io.github.frankois944.spmForKmp") version "0.3.3"
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.compilations {
            val main by getting {
                cinterops.create("nativeIosShared")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.kmp)
            implementation(libs.kotlinx.serialization)

            // Compose
            implementation(compose.components.resources)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)

            // Firebase
            implementation(libs.firebase.kmp.auth)

            // Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)

            // Modules
            implementation(project(Modules.AUTHENTICATION_DATA))
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.DI))
            implementation(project(Modules.LOGIN_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))

            // Voyager
            implementation(libs.voyager.screen.model)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.koin)
        }

        androidMain.dependencies {
            implementation(libs.saved.state.kmp)

            // Compose
            implementation(libs.activity.compose)

            // Credentials Manager
            implementation(libs.credentials)
            implementation(libs.credentials.play.services)
            implementation(libs.google.identity)

            // Facebook
            implementation(libs.facebook.android.sdk)

            // Google
            implementation(libs.play.services.auth)
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.login.presentation"
    compileSdk = AndroidConfig.COMPILE_SDK
    buildFeatures.buildConfig = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK

        val secretsPropertiesFile = project.rootProject.file("secrets.properties")
        val secretProperties = Properties()

        if (secretsPropertiesFile.exists()) {
            secretProperties.load(FileInputStream(secretsPropertiesFile))
        }

        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${secretProperties["web_client_id"]}\""
        )
    }

    lint {
        disable += listOf("CoroutineCreationDuringComposition")
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.login.presentation"
    generateResClass = always
}

swiftPackageConfig {
    create("nativeIosShared") {
        minIos = "18.0"
        dependency(
            SwiftDependency.Package.Remote.Version(
            url = URI("https://github.com/firebase/firebase-ios-sdk.git"),
            products = {
                add("FirebaseCore", "FirebaseAuth", exportToKotlin = true)
            },
            version = "11.8.1"
        ),
            SwiftDependency.Package.Remote.Version(
                url = URI("https://github.com/google/GoogleSignIn-iOS"),
                products = {
                    add("GoogleSignIn", exportToKotlin = true)
                },
                version = "8.0.0"
            )
        )
    }
}
