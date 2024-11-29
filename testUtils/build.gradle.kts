plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
}

apply<TestGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.testUtils"
}


