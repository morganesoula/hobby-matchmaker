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
                // Modules
                implementation(project(Modules.DI))
                implementation(project(Modules.COMMON))
                implementation(project(Modules.SESSION_DOMAIN))
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.authentication.domain"
}
