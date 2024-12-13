plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<TestGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.testUtils.core"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Facebook
    implementation(libs.facebook.android.sdk)
    implementation(libs.facebook.login)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Modules
    implementation(project(Modules.AUTHENTICATION_DOMAIN))
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DI))
    implementation(project(Modules.LOGIN_DOMAIN))
    implementation(project(Modules.LOGIN_PRESENTATION))
    implementation(project(Modules.SESSION_DOMAIN))

    // Test
    testImplementation(project(Modules.TEST_COMMON))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.ktx)
    testImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
}
