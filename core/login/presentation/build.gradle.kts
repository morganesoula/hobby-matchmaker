import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.gradle.kotlin.dsl.configure
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.spm.kmp)
    alias(libs.plugins.build.konfig)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
        namespace = "com.msoula.hobbymatchmaker.core.login.presentation"

        compilerOptions {
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.msoula.hobbymatchmaker.core.login.presentation.models.HMMParcelize"
            )
        }
    }

    iosArm64().apply {
        compilations["main"].cinterops.create("nativeIosAuthShared")
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
        }

        commonMain.dependencies {
            // Modules
            implementation(project(Modules.AUTHENTICATION_DOMAIN))
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.LOGIN_DOMAIN))
            implementation(project(Modules.PROFILE_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("saved-state-kmp").get())

            // Compose
            implementation(libs.findLibrary("activity-compose").get())

            // Credentials Manager
            implementation(libs.findLibrary("credentials").get())
            implementation(libs.findLibrary("credentials-play-services").get())
            implementation(libs.findLibrary("google-identity").get())

            // Facebook
            implementation(libs.findLibrary("facebook-android-sdk").get())
            implementation(libs.findLibrary("facebook-login").get())

            // Google
            implementation(libs.findLibrary("play-services-auth").get())
        }
    }
}

buildkonfig {
    packageName = "com.msoula.hobbymatchmaker.core.login.presentation"

    defaultConfigs {
        val secretsPropertiesFile = project.rootProject.file("secrets.properties")
        val secretProperties = Properties()

        if (secretsPropertiesFile.exists()) {
            secretProperties.load(FileInputStream(secretsPropertiesFile))
        }

        buildConfigField(
            STRING,
            "WEB_CLIENT_ID",
            secretProperties["web_client_id"]?.toString() ?: ""
        )
    }
}

swiftPackageConfig {
    create("nativeIosAuthShared") {
        minIos = "18.0"
    }
}
