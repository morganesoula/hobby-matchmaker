plugins {
    `android-library`
    `kotlin-android`
    kotlin(Plugins.KAPT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.database.dao"
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Room
    api(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
}
