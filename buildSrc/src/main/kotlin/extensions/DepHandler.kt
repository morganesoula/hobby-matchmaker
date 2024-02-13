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
    implementation(Deps.AndroidX.Compose.PREVIEW)
    androidTestImplementation(composeBom)

    // Core
    implementation(Deps.AndroidX.Compose.MATERIAL)
    implementation(Deps.AndroidX.Compose.MATERIAL3)

    implementation(Deps.AndroidX.Compose.ACTIVITY)
    implementation(Deps.AndroidX.Compose.VIEW_MODEL)

    // AndroidX
    implementation(Deps.AndroidX.CORE_KTX)
    implementation(Deps.AndroidX.APP_COMPAT)
    implementation(Deps.AndroidX.Activity.ACTIVITY_KTX)
    implementation(Deps.AndroidX.Lifecycle.LIFECYCLE_RUNTIME)

    // Facebook
    implementation(Deps.Facebook.FACEBOOK_SDK)

    // Firebase
    implementation(platform(Deps.Firebase.FIREBASE_BOM))
    implementation(Deps.Firebase.FIREBASE_AUTH)
    implementation(Deps.Firebase.FIREBASE_UI_AUTH)

    // Hilt
    implementation(Deps.Dagger.HILT_ANDROID)
    implementation(Deps.Dagger.HILT_ANDROID_COMPILER)
    implementation(Deps.Dagger.HILT_NAVIGATION_COMPOSE)

    // Lifecycle
    implementation(Deps.AndroidX.Lifecycle.LIFECYCLE_RUNTIME)

    // Libraries
    implementation(project(Modules.AUTH))
    implementation(project(Modules.NETWORK))
    implementation(project(Modules.DI))
    implementation(project(Modules.MODEL))
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.NAVIGATION))

    implementation(Deps.AndroidX.Compose.MATERIAL)
    api(Deps.AndroidX.APP_COMPAT)

    // Navigation
    implementation(Deps.AndroidX.Navigation.NAVIGATION_UI)

    // Hilt
    implementation(Deps.Dagger.HILT_ANDROID)
    kapt(Deps.Dagger.HILT_ANDROID_COMPILER)
}

fun DependencyHandler.authModuleDeps() {
    implementation(Deps.AndroidX.Compose.MATERIAL3)
    implementation(Deps.AndroidX.Compose.PREVIEW)
    androidTestImplementation(Deps.AndroidX.Compose.UI_TEST)

    implementation(Deps.AndroidX.Compose.ACTIVITY)
    implementation(Deps.AndroidX.Compose.VIEW_MODEL)

    // Coroutine
    implementation(Deps.Coroutines.CORE)

    // DI
    implementation(project(Modules.DI))

    // Facebook
    implementation(Deps.Facebook.FACEBOOK_SDK)
    implementation(Deps.Facebook.FACEBOOK_LOGIN)

    // Firebase
    implementation(platform(Deps.Firebase.FIREBASE_BOM))
    implementation(Deps.Firebase.FIREBASE_AUTH)
    implementation(Deps.Google.PLAY_SERVICES)

    // Hilt
    implementation(Deps.Dagger.HILT_ANDROID)
    kapt(Deps.Dagger.HILT_ANDROID_COMPILER)

    // Retrofit
    implementation(Deps.Retrofit.RETROFIT)
    implementation(Deps.Retrofit.MOSHI_CONVERTER)
    implementation(Deps.OkHttp.OK_HTTP)
    implementation(Deps.OkHttp.LOGGING)

    // Librairie
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.NAVIGATION))
}

fun DependencyHandler.coreNetworkModuleDeps() {
    implementation(project(Modules.DESIGN))
}

fun DependencyHandler.coreDiModuleDeps() {
    // Core
    implementation(Deps.AndroidX.CORE_KTX)
    implementation(Deps.AndroidX.APP_COMPAT)

    // Hilt
    implementation(Deps.Dagger.HILT_ANDROID)
    kapt(Deps.Dagger.HILT_ANDROID_COMPILER)

    // Firebase
    implementation(Deps.Firebase.FIREBASE_CORE_KTX)

    // Librairie
    implementation(project(Modules.DESIGN))

    // Navigation
    implementation(Deps.AndroidX.Navigation.COMPOSE_NAVIGATION)
}

fun DependencyHandler.coreModelModuleDeps() {
    implementation(project(Modules.DESIGN))
}

fun DependencyHandler.coreDesignModuleDeps() {
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(Deps.AndroidX.Compose.MATERIAL)
    implementation(Deps.AndroidX.Compose.MATERIAL3)

    // Core
    implementation(Deps.AndroidX.CORE_KTX)
}

fun DependencyHandler.coreNavigationModuleDeps() {
    implementation(Deps.AndroidX.Navigation.NAVIGATION_UI)

    // Librairie
    implementation(project(Modules.DESIGN))

    // Compose
    implementation(Deps.AndroidX.Compose.ACTIVITY)

    // Navigation
    implementation(Deps.AndroidX.Navigation.NAVIGATION_UI)
    implementation(Deps.AndroidX.Navigation.COMPOSE_NAVIGATION)
}

fun DependencyHandler.unitTestDeps() {
    // (Required) writing and executing Unit Tests on the JUnit Platform
    testImplementation(TestDeps.AssertK.ASSERTK)
    testImplementation(TestDeps.Dagger.HILT_ANDROID_TESTING)

    // AndroidX Test - JVM testing
    testImplementation(TestDeps.AndroidX.CORE_KTX)

    // Coroutines Test
    testImplementation(TestDeps.Coroutines.COROUTINES)

    // JUnit5
    testImplementation(TestDeps.JUNIT.JUNIT_JUPITER)

    // MocKK
    testImplementation(TestDeps.MockK.MOCKK)
    testImplementation(TestDeps.MockK.MOCKK_ANDROID)

    // Truth
    testImplementation(TestDeps.TRUTH)
}

fun DependencyHandler.instrumentationTestDeps() {
    // AndroidX Test - Instrumented testing
    androidTestImplementation(TestDeps.AndroidX.ANDROIDX_JUNIT)
    androidTestImplementation(TestDeps.AndroidX.CORE_TESTING)

    // Navigation Testing
    androidTestImplementation(TestDeps.AndroidX.NAVIGATION_TEST)

    // Coroutines Test
    androidTestImplementation(TestDeps.Coroutines.COROUTINES)

    // MockK
    androidTestImplementation(TestDeps.MockK.MOCKK)

    // Truth
    androidTestImplementation(TestDeps.TRUTH)
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
    configuration: String? = null,
): ProjectDependency? {
    val notation =
        if (configuration != null) {
            mapOf("path" to path, "configuration" to configuration)
        } else {
            mapOf("path" to path)
        }

    return uncheckedCast(project(notation))
}
