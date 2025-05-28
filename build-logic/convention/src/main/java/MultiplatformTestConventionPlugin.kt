import com.msoula.convention.configureUnitTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformTestConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs

        with (pluginManager) {
            apply("hobbymatchmaker.buildlogic.multiplatformMinimalist")
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            configureUnitTest(libs)
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
