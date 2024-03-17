import extensions.featureMoviesPresentationDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.presentation"
}

dependencies {
    featureMoviesPresentationDeps()
    unitTestDeps()
}
