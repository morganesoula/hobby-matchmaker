plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.KSP)
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
    ksp(libs.koin.ksp)

    // Modules
    implementation(project(Modules.COMMON))
}
