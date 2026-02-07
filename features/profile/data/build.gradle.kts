plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.features.profile.data"
    }

    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.findBundle("ktor").get())

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.NETWORK))
            implementation(project(Modules.PROFILE_DOMAIN))
        }
    }
}
