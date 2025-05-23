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
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.di"
}
