import com.android.build.api.dsl.ApplicationExtension
import com.msoula.convention.MultiplatformConfigExtension
import com.msoula.convention.configureAndroidApplication
import com.msoula.convention.configureCompose
import com.msoula.convention.configureIOSApplication
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
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

            // Task if you modify iosMain
            target.tasks.register<Sync>("exportComposeAppXCFramework") {
                group = "iOS"
                description = "Export XCFramework for composeApp to a persistent folder"

                dependsOn("assembleXCFramework")

                val inputDir = target.layout.buildDirectory.dir("XCFrameworks/debug/composeApp.xcframework").get().asFile
                val outputDir = target.rootDir.resolve("composeApp/xcframeworks/composeApp.xcframework")

                from(inputDir)
                into(outputDir)

                doFirst {
                    logger.lifecycle("📦 Exporting composeApp.xcframework to $outputDir")
                }
            }

            // Task if you modify commonMain
            tasks.register<Sync>("copyDebugXCFrameworkToXcode") {
                group = "iOS"
                description = "Just copy existing XCFramework to stable folder (no rebuild)"

                val inputDir = layout.buildDirectory
                    .dir("XCFrameworks/debug/composeApp.xcframework")
                    .get()
                    .asFile

                val outputDir = rootDir
                    .resolve("composeApp/xcframeworks/composeApp.xcframework")

                from(inputDir)
                into(outputDir)

                doFirst {
                    logger.lifecycle("🚀 Copying XCFramework only (no rebuild)")
                }
            }

        }
    }
}
