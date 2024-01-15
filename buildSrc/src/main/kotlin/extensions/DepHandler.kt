package extensions

import Deps
import Modules
import TestDeps
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.internal.Cast.uncheckedCast
import org.gradle.kotlin.dsl.project

fun DependencyHandler.appModuleDeps() {
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(Deps.AndroidX.Compose.material3)
    implementation(Deps.AndroidX.Compose.preview)
    androidTestImplementation(Deps.AndroidX.Compose.uiTest)

    implementation(Deps.AndroidX.Compose.activity)
    implementation(Deps.AndroidX.Compose.viewModel)

    // AndroidX
    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.AndroidX.Activity.activityKtx)
    implementation(Deps.AndroidX.Lifecycle.runtime)

    // Firebase
    implementation(platform(Deps.Firebase.firebaseBom))
    implementation(Deps.Firebase.firebaseAuth)
    implementation(Deps.Firebase.firebaseUiAuth)

    // Hilt
    implementation(Deps.Dagger.hiltAndroid)
    implementation(Deps.Dagger.hiltAndroidCompiler)

    // Lifecycle
    implementation(Deps.AndroidX.Lifecycle.runtime)

    // Libraries
    implementation(project(Modules.auth))
    implementation(project(Modules.network))
    implementation(project(Modules.di))
    implementation(project(Modules.model))

    api(Deps.Android.material)
    api(Deps.AndroidX.appCompat)

    // Navigation
    implementation(Deps.AndroidX.Navigation.ui)

    // Hilt
    implementation(Deps.Dagger.hiltAndroid)
    kapt(Deps.Dagger.hiltAndroidCompiler)
}

fun DependencyHandler.authModuleDeps() {
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(Deps.AndroidX.Compose.material3)
    implementation(Deps.AndroidX.Compose.preview)
    androidTestImplementation(Deps.AndroidX.Compose.uiTest)

    implementation(Deps.AndroidX.Compose.activity)
    implementation(Deps.AndroidX.Compose.viewModel)

    // Firebase
    implementation(platform(Deps.Firebase.firebaseBom))
    implementation(Deps.Firebase.firebaseAuth)
    implementation(Deps.Firebase.firebaseUiAuth)

    // Hilt
    implementation(Deps.Dagger.hiltAndroid)
    kapt(Deps.Dagger.hiltAndroidCompiler)

    // Retrofit
    implementation(Deps.Retrofit.retrofit)
    implementation(Deps.Retrofit.moshiConverter)
    implementation(Deps.OkHttp.okHttp)
    implementation(Deps.OkHttp.logging)
}

fun DependencyHandler.coreNetworkModuleDeps() {
}

fun DependencyHandler.coreDiModuleDeps() {
    // Core
    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.Android.material)

    // Hilt
    implementation(Deps.Dagger.hiltAndroid)
    kapt(Deps.Dagger.hiltAndroidCompiler)
}

fun DependencyHandler.coreModelModuleDeps() {
}

fun DependencyHandler.unitTestDeps() {
    // (Required) writing and executing Unit Tests on the JUnit Platform
    testImplementation(TestDeps.JUnit.junit)

    // AndroidX Test - JVM testing
    testImplementation(TestDeps.AndroidX.coreKtx)

    // Coroutines Test
    testImplementation(TestDeps.Coroutines.coroutines)

    // MocKK
    testImplementation(TestDeps.MockK.mockK)

    // Truth
    testImplementation(TestDeps.truth)
}

fun DependencyHandler.instrumentationTestDeps() {
    // AndroidX Test - Instrumented testing
    androidTestImplementation(TestDeps.AndroidX.androidX_jUnit)
    androidTestImplementation(TestDeps.AndroidX.coreTesting)

    // Navigation Testing
    androidTestImplementation(TestDeps.AndroidX.navigationTest)

    // Coroutines Test
    androidTestImplementation(TestDeps.Coroutines.coroutines)

    // MockK
    androidTestImplementation(TestDeps.MockK.mockK)

    // Truth
    androidTestImplementation(TestDeps.truth)
}

@Suppress("detekt.UnusedPrivateMember")
private fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)

@Suppress("detekt.UnusedPrivateMember")
private fun DependencyHandler.api(dependencyNotation: Any): Dependency? =
    add("api", dependencyNotation)

@Suppress("detekt.UnusedPrivateMember")
private fun DependencyHandler.kapt(dependencyNotation: Any): Dependency? =
    add("kapt", dependencyNotation)

private fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

private fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)

private fun DependencyHandler.project(
    path: String,
    configuration: String? = null
): ProjectDependency? {
    val notation = if (configuration != null) {
        mapOf("path" to path, "configuration" to configuration)
    } else {
        mapOf("path" to path)
    }

    return uncheckedCast(project(notation))
}