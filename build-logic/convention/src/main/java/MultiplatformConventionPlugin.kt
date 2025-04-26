import com.android.build.gradle.LibraryExtension
import com.msoula.convention.MultiplatformConfigExtension
import com.msoula.convention.configureCInterops
import com.msoula.convention.configureMultiplatform
import com.msoula.convention.configureMultiplatformAndroid
import com.msoula.convention.configureMultiplatformIos
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs
        val config = MultiplatformConfigExtension()
        target.extensions.add("multiplatformConfig", config)

        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.multiplatform")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        target.afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                configureMultiplatformIos()
                configureMultiplatform(libs, config)
                configureCInterops(this@with)
            }
        }

        extensions.configure<LibraryExtension> {
            configureMultiplatformAndroid()
        }
    }
}
