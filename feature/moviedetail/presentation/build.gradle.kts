plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.presentation"
}

dependencies {
    // AndroidX
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.material.icons)

    // Coil
    implementation(libs.coil.compose)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Compose
    implementation(libs.activity.compose)
    implementation(libs.material3)
    implementation(libs.ui.tooling)

    // Exoplayer
    implementation(libs.media3.common)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.MOVIE_DETAIL_DOMAIN))

    // Test
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.ktx)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)

    // Youtube
    implementation(libs.youtube.player)
}
