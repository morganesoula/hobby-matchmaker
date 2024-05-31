import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.Properties

class MainGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        applyPlugins(project)
        setProjectConfig(project)
    }

    private fun applyPlugins(project: Project) {
        project.apply {
            plugin(Plugins.ANDROID_LIBRARY)
            plugin(Plugins.KOTLIN_ANDROID)
            plugin(Plugins.KOTLIN_KAPT)
            plugin("kotlin-parcelize")
        }
    }

    private fun setProjectConfig(project: Project) {
        val tmdbPropertiesFile = project.rootProject.file("./tmdb.properties")
        val tmdbProperties = Properties()

        if (tmdbPropertiesFile.exists()) {
            tmdbProperties.load(tmdbPropertiesFile.inputStream())
        }

        project.android().apply {
            compileSdk = AndroidConfig.COMPILE_SDK

            defaultConfig {
                minSdk = AndroidConfig.MIN_SDK
                testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER

                buildConfigField("String", "TMDB_KEY", "\"${tmdbProperties["tmdb_key"]}\"")
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            buildFeatures {
                buildConfig = true
                compose = true
            }

            composeOptions {
                kotlinCompilerExtensionVersion = "1.5.8"
            }
        }
    }

    private fun Project.android(): LibraryExtension {
        return extensions.getByType(LibraryExtension::class.java)
    }
}
