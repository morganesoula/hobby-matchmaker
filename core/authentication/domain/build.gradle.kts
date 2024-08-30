plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.KSP)
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.domain"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Koin
    implementation(libs.koin.android)
    ksp(libs.koin.ksp)

    // Modules
    implementation(project(Modules.COMMON))
}
