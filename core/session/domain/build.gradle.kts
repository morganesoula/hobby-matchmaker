plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // com.msoula.convention.Modules
                implementation(project(Modules.COMMON))
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.session.domain"
}
