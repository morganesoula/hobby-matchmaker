plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Ktor
                implementation(libs.findBundle("ktor").get())

                // Modules
                implementation(project(Modules.NETWORK))
                implementation(project(Modules.DATABASE))
                implementation(project(Modules.MOVIE_DOMAIN))
                implementation(project(Modules.COMMON))
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.data"
}
