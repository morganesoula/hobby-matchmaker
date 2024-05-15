plugins {
    `android-library`
    `kotlin-android`
    kotlin(Plugins.KAPT)
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

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.DESIGN))

    // Navigation
    implementation(libs.navigation.ui.ktx)
    implementation(libs.compose.navigation)
}
