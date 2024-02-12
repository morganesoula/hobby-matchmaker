import extensions.coreDiModuleDeps
import extensions.instrumentationTestDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.di"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    coreDiModuleDeps()
    unitTestDeps()
    instrumentationTestDeps()
}
