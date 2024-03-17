import extensions.coreDatabaseModuleDeps
import extensions.instrumentationTestDeps

plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.database"
}

dependencies {
    coreDatabaseModuleDeps()
    instrumentationTestDeps()
}
