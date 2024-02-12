import extensions.coreModelModuleDeps
import extensions.instrumentationTestDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.model"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    coreModelModuleDeps()
    unitTestDeps()
    instrumentationTestDeps()
}
