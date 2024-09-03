plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.database"
}

dependencies {
    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.DAO))

    // Paging
    implementation(libs.paging.compose)

    // Room
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}
