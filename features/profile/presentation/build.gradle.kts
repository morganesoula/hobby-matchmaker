plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.profile.presentation"
}

