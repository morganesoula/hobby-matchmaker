plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.KSP)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.database"
}

dependencies {
    // Koin
    implementation(libs.koin.android)
    ksp(libs.koin.ksp)

    // Modules
    implementation(project(Modules.DAO))

    // Paging
    implementation(libs.paging.compose)

    // Room
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}
