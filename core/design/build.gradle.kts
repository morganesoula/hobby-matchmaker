plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.compose)
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.design"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.msoula.hobbymatchmaker.core.design"
    generateResClass = always
}
