plugins {
    `android-library`
    `kotlin-android`
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
    implementation(libs.runtime)

    // Modules
    implementation(project(Modules.DESIGN))

    // Retrofit
    implementation(libs.retrofit)
}
