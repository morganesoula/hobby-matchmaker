plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.KSP)
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
    ksp(libs.koin.ksp)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DAO))
    implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
    implementation(project(Modules.NETWORK))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
}
