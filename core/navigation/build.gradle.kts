import extensions.coreNavigationModuleDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.navigation"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    coreNavigationModuleDeps()
    unitTestDeps()
}
