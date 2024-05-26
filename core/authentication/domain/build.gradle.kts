plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
    kotlin(Plugins.KAPT)
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

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.SESSION_DOMAIN))
}
