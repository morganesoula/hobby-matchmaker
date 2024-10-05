plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.design"
}

dependencies {
    // Compose
    implementation(libs.material3)
    implementation(libs.runtime)
    implementation(libs.material.icons)

    // Core
    implementation(libs.core.ktx)
}
