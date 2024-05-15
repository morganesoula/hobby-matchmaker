plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.design"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    // Compose
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.runtime)
    implementation(libs.material.icons)

    // Core
    implementation(libs.core.ktx)
}
