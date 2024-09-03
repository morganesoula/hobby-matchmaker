plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
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

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.NAVIGATION))
    implementation(project(Modules.DESIGN))
}
