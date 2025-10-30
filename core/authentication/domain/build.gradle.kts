plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    alias(libs.plugins.kover)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.SESSION_DOMAIN))
        }

        commonTest.dependencies {
            implementation(libs.findLibrary("kotlinx-coroutines-test").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.domain"
}
