plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.data"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Firestore
    implementation(libs.firebase.firebase.firestore.ktx)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
    implementation(project(Modules.NETWORK))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
}
