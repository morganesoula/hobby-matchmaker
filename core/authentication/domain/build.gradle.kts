plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.core.authentication.domain"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.PROFILE_DOMAIN))
            implementation(project(Modules.SESSION_DOMAIN))
        }

        commonTest.dependencies {
            implementation(libs.findLibrary("kotlinx-coroutines-test").get())
        }
    }
}
