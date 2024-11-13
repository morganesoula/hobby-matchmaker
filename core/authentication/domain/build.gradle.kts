plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.domain"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Facebook
    implementation(libs.facebook.android.sdk)
    implementation(libs.facebook.login)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.SESSION_DOMAIN))
}
