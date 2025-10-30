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
        register("multiplatformNoIos") {
            id = "hobbymatchmaker.buildlogic.multiplatformNoIos"
            implementationClass = "MultiplatformNoIosConventionPlugin"
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
        register("multiplatformMinimalistNoIos") {
            id = "hobbymatchmaker.buildlogic.multiplatformMinimalistNoIos"
            implementationClass = "MultiplatformMinimalistNoIosConventionPlugin"
        }
    }

    plugins {
        register("application") {
            id = "hobbymatchmaker.buildlogic.application"
            implementationClass = "ApplicationConventionPlugin"
        }
    }

    plugins {
        register("multiplatformTest") {
            id = "hobbymatchmaker.buildlogic.multiplatformTest"
            implementationClass = "MultiplatformTestConventionPlugin"
        }
    }
}
