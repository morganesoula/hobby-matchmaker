import com.msoula.convention.configureUnitTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformTestConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            configureUnitTest(libs)
        }
    }
}
