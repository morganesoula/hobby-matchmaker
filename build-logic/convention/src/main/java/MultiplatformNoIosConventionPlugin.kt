import com.android.build.gradle.LibraryExtension
import com.msoula.convention.MultiplatformConfigExtension
import com.msoula.convention.configureMultiplatform
import com.msoula.convention.configureMultiplatformAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformNoIosConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = this.libs
        val config = MultiplatformConfigExtension()
        target.extensions.add("multiplatformConfig", config)

        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            jvm()
        }

        afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                configureMultiplatform(libs, config)
            }
        }

        extensions.configure<LibraryExtension> {
            configureMultiplatformAndroid()
        }
    }

}
