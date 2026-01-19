import com.msoula.convention.MultiplatformConfigExtension
import com.msoula.convention.configureAndroidLibrary
import com.msoula.convention.configureCompose
import com.msoula.convention.configureMultiplatformIos
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = this.libs

        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.kotlin.multiplatform.library")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }

        val compose = extensions.getByType(ComposeExtension::class.java).dependencies
        val config = MultiplatformConfigExtension(target, libs, compose)
        target.extensions.add("multiplatformConfig", config)

        extensions.configure<KotlinMultiplatformExtension> {
            configureAndroidLibrary(this@with)
            configureMultiplatformIos(this@with)
            configureCompose(libs)
        }
    }
}
