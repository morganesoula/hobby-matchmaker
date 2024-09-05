plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
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

    // Firebase
    implementation(libs.firebase.firebase.auth.ktx)

    // Modules
    implementation(project(Modules.DESIGN))

    // Navigation
    implementation(libs.compose.navigation)
}
