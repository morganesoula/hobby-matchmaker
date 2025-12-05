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
            apply("org.jetbrains.compose")

            // Only apply Kotzilla if kotzilla.json exists in the module or at root
            val moduleKotzillaFile = file("kotzilla.json")
            val rootKotzillaFile = rootProject.file("kotzilla.json")
            if (moduleKotzillaFile.exists() || rootKotzillaFile.exists()) {
                apply("io.kotzilla.kotzilla-plugin")

                // Create symlink if only root file exists
                if (!moduleKotzillaFile.exists() && rootKotzillaFile.exists()) {
                    try {
                        java.nio.file.Files.createSymbolicLink(
                            moduleKotzillaFile.toPath(),
                            rootKotzillaFile.toPath()
                        )
                    } catch (e: Exception) {
                        logger.warn("Could not create symlink for kotzilla.json: ${e.message}")
                    }
                }
            }
        }

        val compose = extensions.getByType(ComposeExtension::class.java).dependencies

        target.afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                configureMultiplatformIos(this@with)
                configureCompose(libs, config, compose)
            }
        }

        extensions.configure<LibraryExtension> {
            configureMultiplatformAndroid()
        }
    }
}
