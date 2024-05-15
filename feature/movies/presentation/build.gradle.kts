plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.KAPT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.presentation"
}

dependencies {
    // AndroidX
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.collections.immutable)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Compose
    implementation(libs.activity.compose)
    implementation(libs.material3)
    implementation(libs.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.MOVIE_DOMAIN))
    implementation(project(Modules.COMMON))
    implementation(project(Modules.AUTHENTICATION_DOMAIN))
}
