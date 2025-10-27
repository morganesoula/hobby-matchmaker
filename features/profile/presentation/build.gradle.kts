plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation("androidx.compose.ui:ui-tooling:1.9.4")
            implementation("androidx.compose.ui:ui-tooling-preview:1.9.4")
        }

        commonMain.dependencies {
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.PROFILE_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.profile.presentation"
}

