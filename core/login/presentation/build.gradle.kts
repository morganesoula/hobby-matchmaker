plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}
apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.login.presentation"
}

dependencies {
    // AndroidX
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Compose
    implementation(libs.activity.compose)
    implementation(libs.material3)
    implementation(libs.ui.tooling)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Credentials Manager
    implementation(libs.credentials)
    implementation(libs.credentials.play.services)
    implementation(libs.google.identity)

    // Facebook
    implementation(libs.facebook.android.sdk)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Google
    implementation(libs.play.services.auth)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Modules
    implementation(project(Modules.AUTHENTICATION_DATA))
    implementation(project(Modules.AUTHENTICATION_DOMAIN))
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.DI))
    implementation(project(Modules.LOGIN_DOMAIN))
    implementation(project(Modules.NAVIGATION))
    implementation(project(Modules.SESSION_DOMAIN))
}
