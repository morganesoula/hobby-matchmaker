plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.minimalist)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.findBundle("ktor").get())

            // Modules
            implementation(project(Modules.COMMON))
            implementation(project(Modules.DATABASE))
            implementation(project(Modules.MOVIE_DETAIL_DOMAIN))
            implementation(project(Modules.MOVIE_DATA))
            implementation(project(Modules.NETWORK))
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.moviedetail.data"
}
