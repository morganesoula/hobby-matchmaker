import com.android.build.gradle.LibraryExtension
import com.msoula.convention.MultiplatformConfigExtension
import com.msoula.convention.configureCompose
import com.msoula.convention.configureMultiplatformAndroid
import com.msoula.convention.configureMultiplatformIos
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs
        val config = MultiplatformConfigExtension()
        target.extensions.add("multiplatformConfig", config)

        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.multiplatform")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("org.jetbrains.kotlin.plugin.compose")
        }

        val compose = extensions.getByType(ComposeExtension::class.java).dependencies

        target.afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                configureMultiplatformIos()
                configureCompose(libs, config, compose)
            }
        }

        extensions.configure<LibraryExtension> {
            configureMultiplatformAndroid()
        }
    }
}
