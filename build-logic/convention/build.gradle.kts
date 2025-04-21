plugins {
    `kotlin-dsl`
}

group = "com.msoula.hobbymatchmaker.buildlogic"

dependencies {
    compileOnly(libs.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("multiplatform") {
            id = "hobbymatchmaker.buildlogic.multiplatform"
            implementationClass = "MultiplatformConventionPlugin"
        }
    }

    plugins {
        register("multiplatformCompose") {
            id = "hobbymatchmaker.buildlogic.multiplatformCompose"
            implementationClass = "MultiplatformComposeConventionPlugin"
        }
    }

    plugins {
        register("multiplatformMinimalist") {
            id = "hobbymatchmaker.buildlogic.multiplatformMinimalist"
            implementationClass = "MultiplatformMinimalistPlugin"
        }
    }

    plugins {
        register("application") {
            id = "hobbymatchmaker.buildlogic.application"
            implementationClass = "ApplicationConventionPlugin"
        }
    }
}
