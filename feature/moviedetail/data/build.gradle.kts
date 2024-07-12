plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.KAPT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.data"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
    implementation(project(Modules.NETWORK))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
}
