plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
    kotlin(Plugins.KAPT)
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

    // Facebook
    implementation(libs.facebook.android.sdk)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Google
    implementation(libs.play.services.auth)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.AUTHENTICATION_DOMAIN))
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.DI))
    implementation(project(Modules.LOGIN_DOMAIN))
}
