plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DESIGN))
            implementation(project(Modules.SOCIAL_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.social.presentation"
}
