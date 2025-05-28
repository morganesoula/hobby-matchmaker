plugins {
    alias(libs.plugins.hobbymatchmaker.buildlogic.multiplatform.test)
    id("io.kotest.multiplatform") version "5.9.1"
}

android {
    namespace = "com.msoula.hobbymatchmaker.testUtils.core.di"
}
