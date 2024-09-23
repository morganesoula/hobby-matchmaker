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

    //Room Workaround
    implementation("androidx.navigation:navigation-compose:2.8.1")

    //Room
    api(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}
