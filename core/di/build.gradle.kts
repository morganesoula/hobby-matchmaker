plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.KSP)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.di"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    // Core
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.runtime)

    // Koin
    implementation(libs.koin.android)
    ksp(libs.koin.ksp)

    // Firebase
    implementation(libs.firebase.firebase.auth.ktx)

    // Modules
    implementation(project(Modules.DESIGN))

    // Navigation
    implementation(libs.compose.navigation)

    // Room
    api(libs.room.runtime)
}
