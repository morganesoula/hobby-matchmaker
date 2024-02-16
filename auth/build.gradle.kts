import extensions.authModuleDeps
import extensions.unitTestDeps

plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.auth"
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add(("META-INF/**"))
    }
}

dependencies {
    authModuleDeps()
    unitTestDeps()
}
