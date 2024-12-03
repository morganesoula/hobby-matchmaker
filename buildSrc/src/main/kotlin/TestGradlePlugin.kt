import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class TestGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        applyPlugins(project)
        setProjectConfig(project)
    }

    private fun applyPlugins(project: Project) {
        project.apply {
            plugin(Plugins.ANDROID_LIBRARY)
            plugin(Plugins.KOTLIN_ANDROID)
        }
    }

    private fun setProjectConfig(project: Project) {
        project.android().apply {
            compileSdk = AndroidConfig.COMPILE_SDK

            defaultConfig {
                minSdk = AndroidConfig.MIN_SDK
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }

            testOptions {
                unitTests.apply {
                    isReturnDefaultValues = true
                    all { testTask ->
                        testTask.jvmArgs("-XX:+EnableDynamicAgentLoading")
                    }
                }
            }
        }
    }
}

private fun Project.android(): LibraryExtension {
    return extensions.getByType(LibraryExtension::class.java)
}

