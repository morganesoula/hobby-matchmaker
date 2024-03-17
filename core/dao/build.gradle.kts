import extensions.coreDaoModuleDeps
import extensions.instrumentationTestDeps

plugins {
    `android-library`
    `kotlin-android`
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.dao"
}

dependencies {
    coreDaoModuleDeps()
    instrumentationTestDeps()
}
