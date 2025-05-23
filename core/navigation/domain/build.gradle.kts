plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform)
}

multiplatformConfig {
    useDecompose()
}

android {
    namespace = "com.msoula.hobbymatchmaker.core.navigation.domain"
}
