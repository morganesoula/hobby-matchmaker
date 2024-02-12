import extensions.coreDesignModuleDeps
import extensions.instrumentationTestDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.design"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    coreDesignModuleDeps()
    unitTestDeps()
    instrumentationTestDeps()
}
