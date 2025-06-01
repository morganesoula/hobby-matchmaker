import com.msoula.convention.configureUnitTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformTestConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = this.libs

        // In order to unit test, JVM is necessary for KMP
        extensions.configure<KotlinMultiplatformExtension> {
            jvm()
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            configureUnitTest(libs)
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                events("passed", "failed")
                exceptionFormat = TestExceptionFormat.FULL
                showStandardStreams = true
            }
        }
    }
}
