plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
}

multiplatformConfig {
    useFirebase()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // DataStore
            implementation(libs.findLibrary("datastore-preferences").get())

            implementation(project(Modules.COMMON))
            implementation(project(Modules.SESSION_DOMAIN))
        }

        androidMain.dependencies {
            // Koin
            implementation(libs.findLibrary("koin-android").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.session.data"
}
