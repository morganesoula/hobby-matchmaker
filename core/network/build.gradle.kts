plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.network"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Google
    implementation(libs.play.services.auth)

    // Koin
    implementation(libs.koin.android)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.serialization.json)

    // Modules
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.COMMON))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
}
