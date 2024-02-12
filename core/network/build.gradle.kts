import extensions.coreNetworkModuleDeps
import extensions.instrumentationTestDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.network"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    coreNetworkModuleDeps()
    unitTestDeps()
    instrumentationTestDeps()
}
