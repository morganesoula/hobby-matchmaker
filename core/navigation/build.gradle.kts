plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    // Compose
    implementation(libs.activity.compose)
    implementation(libs.runtime)

    // Koin
    implementation(libs.koin.android)

    // Modules
    implementation(project(Modules.DESIGN))

    // Navigation
    implementation(libs.navigation.ui.ktx)
    implementation(libs.compose.navigation)

    // Serialization
    implementation(libs.kotlinx.serialization)
}
