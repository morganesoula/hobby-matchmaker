plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.login.domain"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.COMMON))

    // Unit test
    testImplementation(libs.junit.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
}
