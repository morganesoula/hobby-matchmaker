import com.msoula.convention.MultiplatformConfigExtension
import com.msoula.convention.configureAndroidLibrary
import com.msoula.convention.configureCInterops
import com.msoula.convention.configureMultiplatform
import com.msoula.convention.configureMultiplatformIos
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs
        val config = MultiplatformConfigExtension(target, libs)
        target.extensions.add("multiplatformConfig", config)

        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.kotlin.multiplatform.library")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            configureAndroidLibrary(this@with)
            configureMultiplatformIos(this@with)
            configureMultiplatform(libs)
            configureCInterops(this@with)
        }
    }
}
