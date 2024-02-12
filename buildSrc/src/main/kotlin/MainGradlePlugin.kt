import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class MainGradlePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        applyPlugins(project)
        setProjectConfig(project)
    }

    private fun applyPlugins(project: Project) {
        project.apply {
            plugin(Plugins.ANDROID_LIBRARY)
            plugin(Plugins.KOTLIN_ANDROID)
            plugin(Plugins.KOTLIN_KAPT)
        }
    }

    private fun setProjectConfig(project: Project) {
        project.android().apply {
            compileSdk = AndroidConfig.COMPILE_SDK

            defaultConfig {
                minSdk = AndroidConfig.MIN_SDK
                testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            buildFeatures {
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
