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
            // Ktor
            implementation(libs.findBundle("ktor").get())

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.MOVIE_DOMAIN))
            implementation(project(Modules.NETWORK))
            implementation(project(Modules.PROFILE_DOMAIN))
            implementation(project(Modules.SOCIAL_DOMAIN))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.profile.data"
}
