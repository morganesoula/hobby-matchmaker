plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

multiplatformConfig {
    useDecomposeWithCompose()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.ui:ui-backhandler:1.8.0-alpha03")
            implementation(libs.findLibrary("core-ktx").get())
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.design"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.design"
    generateResClass = always
}
