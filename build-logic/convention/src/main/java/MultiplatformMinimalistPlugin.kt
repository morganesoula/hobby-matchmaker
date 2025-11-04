import com.android.build.gradle.LibraryExtension
import com.msoula.convention.configureMultiplatformAndroid
import com.msoula.convention.configureMultiplatformIos
import com.msoula.convention.configureMultiplatformMinimalist
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformMinimalistPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs

        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.multiplatform")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        // In order to unit test, JVM is necessary for KMP
        extensions.configure<KotlinMultiplatformExtension> {
            jvm()
        }

        target.afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                configureMultiplatformIos(this@with)
                configureMultiplatformMinimalist(libs)
            }
        }

        extensions.configure<LibraryExtension> {
            configureMultiplatformAndroid()
        }
    }
}
