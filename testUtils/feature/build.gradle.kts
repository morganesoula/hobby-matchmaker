plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<TestGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.testUtils.feature"
}

dependencies {
    // Core
    implementation(libs.runtime)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.collections.immutable)

    // Module
    implementation(project(Modules.AUTHENTICATION_DOMAIN))
    implementation(project(Modules.DI))
    implementation(project(Modules.MOVIE_DOMAIN))
    implementation(project(Modules.MOVIE_PRESENTATION))
    implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
    implementation(project(Modules.COMMON))
    testImplementation(project(Modules.TEST_COMMON))

    // Test
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.ktx)
    testImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
}
