plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.database.dao"
}

dependencies {
    //Core
    implementation(libs.runtime)

    //Gson
    implementation(libs.gson)

    //Room
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}
