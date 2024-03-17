import extensions.featureMoviesDomainDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.domain"
}

dependencies {
    featureMoviesDomainDeps()
    unitTestDeps()
}
