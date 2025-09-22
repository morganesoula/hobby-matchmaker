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
                // MODULES
                implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.4")
                implementation(project(":features:movies:presentation"))
                implementation(project(":features:moviedetail:presentation"))
                implementation(project(":features:movies:domain"))
                implementation(project(":features:moviedetail:domain"))
                implementation(project(":features:movies:data"))
                implementation(project(":features:moviedetail:data"))

                implementation(project(":core:authentication:domain"))
                implementation(project(":core:common"))
                implementation(project(":core:database"))
                implementation(project(":core:network"))
                implementation(project(":core:session:data"))
                implementation(project(":core:session:domain"))
                implementation(project(":core:authentication:data"))
                implementation(project(":core:authentication:domain"))
                implementation(project(":core:login:domain"))
                implementation(project(":core:login:presentation"))
                implementation(project(":core:di"))

                // FIREBASE
                implementation(libs.firebase.kmp.firestore)
                implementation(libs.firebase.kmp.auth)

                // TEST
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

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
    }
}
