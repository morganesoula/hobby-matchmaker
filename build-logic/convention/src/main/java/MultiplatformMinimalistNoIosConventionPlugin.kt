import com.android.build.gradle.LibraryExtension
import com.msoula.convention.configureMultiplatformAndroid
import com.msoula.convention.configureMultiplatformMinimalist
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformMinimalistNoIosConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = this.libs

        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            jvm()
        }

        afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                configureMultiplatformMinimalist(libs)
            }
        }

        extensions.configure<LibraryExtension> {
            configureMultiplatformAndroid()
        }
    }
}
