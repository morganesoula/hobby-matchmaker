plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<TestGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.testUtils.common"
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

    // Module
    implementation(project(Modules.AUTHENTICATION_DOMAIN))
    implementation(project(Modules.COMMON))
    implementation(project(Modules.DI))
    implementation(project(Modules.MOVIE_DOMAIN))
    implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
    implementation(project(Modules.SESSION_DOMAIN))
}
