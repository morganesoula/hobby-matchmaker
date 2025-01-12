plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.common"
}

dependencies {
    // Compose
    implementation(libs.activity.compose)
    implementation(libs.runtime)

    // Facebook
    implementation(libs.facebook.android.sdk)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.firestore)

    // Koin
    implementation(libs.koin.android)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.serialization.json)

    // Modules
    implementation(project(Modules.DESIGN))

    // Retrofit
    implementation(libs.retrofit)
}
