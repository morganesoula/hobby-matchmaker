plugins {
    `android-library`
    `kotlin-android`
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
    kotlin(Plugins.KAPT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.splashscreen.presentation"
}

dependencies {
    // Compose
    implementation(libs.activity.compose)
    implementation(libs.material3)
    implementation(libs.ui.tooling)

    // Core
    implementation(libs.runtime)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.NAVIGATION))
    implementation(project(Modules.DESIGN))
}
