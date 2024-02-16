import extensions.featureMoviesModuleDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.example.movies"
}

dependencies {
    featureMoviesModuleDeps()
    unitTestDeps()
}
