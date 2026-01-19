plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    androidLibrary {
        namespace = "com.msoula.hobbymatchmaker.features.social.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.SESSION_DATA))
            implementation(project(Modules.SOCIAL_DOMAIN))
        }
    }
}
