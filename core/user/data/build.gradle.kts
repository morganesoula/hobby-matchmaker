plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.core.user.data"
    }

    sourceSets {
        commonMain.dependencies {
            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.NETWORK))
            implementation(project(Modules.USER_DOMAIN))
        }
    }
}
