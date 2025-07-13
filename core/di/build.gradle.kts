plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.minimalist)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.DESIGN))
        }

        androidMain.dependencies {
            // Core
            implementation(libs.findLibrary("core-ktx").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.di"
}
