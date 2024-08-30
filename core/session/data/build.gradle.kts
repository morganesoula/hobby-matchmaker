plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.KSP)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.session.data"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    // Compose
    implementation(libs.runtime)

    // Datastore
    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)

    // Koin
    implementation(libs.koin.android)
    ksp(libs.koin.ksp)

    // Modules
    implementation(project(Modules.COMMON))
    implementation(project(Modules.SESSION_DOMAIN))
}
