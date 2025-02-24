import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-multiplatform`
    `android-library`
    alias(libs.plugins.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                // FireStore
                implementation(libs.firebase.kmp.firestore)

                // Koin
                implementation(libs.koin.core)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.negotiation)
                implementation(libs.ktor.serialization.json)

                // Modules
                implementation(project(Modules.NETWORK))
                implementation(project(Modules.DAO))
                implementation(project(Modules.MOVIE_DOMAIN))
                implementation(project(Modules.COMMON))

                // Room
                implementation(libs.room.runtime)
            }
        }
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.data"
    compileSdk = AndroidConfig.COMPILE_SDK

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
/* plugins {
    `android-library`
    `kotlin-android`
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.data"
}

dependencies {
    // Arrow
    implementation(libs.arrow.core)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.firestore)

    // Koin
    implementation(libs.koin.android)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.serialization.json)

    // Modules
    implementation(project(Modules.NETWORK))
    implementation(project(Modules.DAO))
    implementation(project(Modules.MOVIE_DOMAIN))
    implementation(project(Modules.COMMON))

    //Room
    api(libs.room.runtime)
    ksp(libs.room.compiler)

    // Test
    testImplementation(libs.junit.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
} */

/*plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.data"
    compileSdk = AndroidConfig.COMPILE_SDK
}*/
