plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.KSP)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.common"
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
    ksp(libs.koin.ksp)

    // Modules
    implementation(project(Modules.DESIGN))

    // Retrofit
    implementation(libs.retrofit)
}
