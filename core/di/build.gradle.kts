plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.minimalist)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core
            implementation(libs.findLibrary("core-ktx").get())

            // Modules
            implementation(project(Modules.DESIGN))
        }

        commonTest.dependencies {
            // Kotest
            implementation(libs.findLibrary("kotest-framework-engine").get())
            implementation(libs.findLibrary("kotest-assertions-core").get())
        }

        jvmTest.dependencies {
            implementation(libs.findLibrary("kotest-runner-junit5").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.di"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
