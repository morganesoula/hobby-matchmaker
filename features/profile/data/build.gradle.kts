plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.PROFILE_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.profile.data"
}
