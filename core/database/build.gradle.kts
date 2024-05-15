plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.KAPT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.database"
}

dependencies {
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.DAO))

    // Paging
    implementation(libs.paging.compose)

    // Room
    api(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
}
