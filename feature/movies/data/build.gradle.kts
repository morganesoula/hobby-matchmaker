plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.data"
}

dependencies {
    // Arrow
    implementation(libs.arrow.core)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.firestore)
    implementation(libs.firebase.firebase.firestore.ktx)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.NETWORK))
    implementation(project(Modules.DAO))
    implementation(project(Modules.MOVIE_DOMAIN))
    implementation(project(Modules.COMMON))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //Room
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // Test
    testImplementation(libs.junit.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
}
