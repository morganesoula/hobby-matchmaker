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

    // Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.firestore.ktx)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.DESIGN))

    // Retrofit
    implementation(libs.retrofit)
}
