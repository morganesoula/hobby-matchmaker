plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    android {
        namespace = "com.msoula.hobbymatchmaker.core.session.domain"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(Modules.COMMON))

                // Uuid
                implementation(libs.findLibrary("uuid").get())
            }
        }
    }
}
