plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
    kotlin(Plugins.KAPT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.domain"
}

dependencies {
    // Arrow
    implementation(libs.arrow.core)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.COMMON))
}
