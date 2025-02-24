plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.domain"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.COMMON))
}
