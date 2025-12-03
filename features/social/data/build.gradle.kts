plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.kover)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.SESSION_DATA))
            implementation(project(Modules.SOCIAL_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.social.data"
}
