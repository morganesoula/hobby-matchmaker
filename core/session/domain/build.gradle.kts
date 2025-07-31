plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(Modules.COMMON))
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.session.domain"
}
