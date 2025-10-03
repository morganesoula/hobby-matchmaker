import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        testRuns["test"].executionTask.configure { useJUnitPlatform() }
    }

    sourceSets {
        val jvmTest by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.3")
                implementation(project(":features:movies:presentation"))
                implementation(project(":features:movies:domain"))
                implementation(project(":features:movies:data"))

                implementation(project(":core:authentication:domain"))
                implementation(project(":core:common"))
                implementation(project(":core:database"))
                implementation(project(":core:network"))

                implementation(libs.firebase.kmp.firestore)

                implementation(libs.koin.test)
                implementation(libs.koin.test.junit.five)
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.turbine)
                implementation(libs.mockk)
                implementation(libs.sqldelight.driver)
            }
        }
    }
}
