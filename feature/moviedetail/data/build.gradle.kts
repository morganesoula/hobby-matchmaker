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

    // Koin
    implementation(libs.koin.android)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.serialization.json)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DAO))
    implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
    implementation(project(Modules.MOVIE_DATA))
    implementation(project(Modules.NETWORK))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
}
