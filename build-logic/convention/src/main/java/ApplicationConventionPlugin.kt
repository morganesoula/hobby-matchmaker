import com.android.build.api.dsl.ApplicationExtension
import com.msoula.convention.MultiplatformConfigExtension
import com.msoula.convention.configureAndroidApplication
import com.msoula.convention.configureCompose
import com.msoula.convention.configureIOSApplication
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs
        val config = MultiplatformConfigExtension()
        target.extensions.add("multiplatformConfig", config)

        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.multiplatform")
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }

        val compose = extensions.getByType(ComposeExtension::class.java).dependencies

        extensions.configure<KotlinMultiplatformExtension> {
            configureIOSApplication()
        }

        target.afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                configureCompose(libs, config, compose)
            }
        }

        extensions.configure<ApplicationExtension> {
            configureAndroidApplication()
        }
    }
}
