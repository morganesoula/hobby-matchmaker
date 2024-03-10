@file:Suppress("ktlint:standard:function-signature")

package extensions

import Deps
import Modules
import TestDeps
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.appModuleDeps() {
    // Compose
    implementation(Deps.AndroidX.Compose.PREVIEW)
    implementation(Deps.AndroidX.Compose.MATERIAL)
    implementation(Deps.AndroidX.Compose.MATERIAL3)
    implementation(Deps.AndroidX.Compose.ACTIVITY)
    implementation(Deps.AndroidX.Compose.RUNTIME)
    implementation(Deps.AndroidX.Compose.VIEW_MODEL)

    // AndroidX
    implementation(Deps.AndroidX.CORE_KTX)
    implementation(Deps.AndroidX.APP_COMPAT)
    implementation(Deps.AndroidX.Activity.ACTIVITY_KTX)
    implementation(Deps.AndroidX.Lifecycle.LIFECYCLE_RUNTIME)
    implementation(Deps.AndroidX.Lifecycle.RUNTIME_COMPOSE)
    implementation(Deps.AndroidX.Splashscreen.SPLASHSCREEN)
    implementation(Deps.AndroidX.Kotlin.KOTLIN_COLLECTION)

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
    implementation(project(Modules.MOVIE))

    implementation(Deps.AndroidX.Compose.MATERIAL)
    api(Deps.AndroidX.APP_COMPAT)

    // Navigation
    implementation(Deps.AndroidX.Navigation.NAVIGATION_UI)

    // Hilt
    implementation(Deps.Dagger.HILT_ANDROID)
    kapt(Deps.Dagger.HILT_ANDROID_COMPILER)
}

fun DependencyHandler.authModuleDeps() {
    // Compose
    implementation(Deps.AndroidX.Compose.MATERIAL3)
    implementation(Deps.AndroidX.Compose.PREVIEW)
    androidTestImplementation(Deps.AndroidX.Compose.UI_TEST)
    implementation(Deps.AndroidX.Compose.ACTIVITY)
    implementation(Deps.AndroidX.Compose.VIEW_MODEL)
    implementation(Deps.AndroidX.Compose.RUNTIME)
    implementation(Deps.AndroidX.Lifecycle.LIFECYCLE_RUNTIME)
    implementation(Deps.AndroidX.Lifecycle.RUNTIME_COMPOSE)

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
    implementation(Deps.Dagger.HILT_NAVIGATION_COMPOSE)
    kapt(Deps.Dagger.HILT_ANDROID_COMPILER)

    // Retrofit
    implementation(Deps.Retrofit.RETROFIT)
    implementation(Deps.OkHttp.OK_HTTP)
    implementation(Deps.OkHttp.LOGGING)

    // Librairie
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.NAVIGATION))
    implementation(project(Modules.NETWORK))
}

fun DependencyHandler.coreNetworkModuleDeps() {
    // Libraries
    implementation(project(Modules.DESIGN))
    implementation(Deps.AndroidX.Compose.RUNTIME)

    // Retrofit
    implementation(Deps.Retrofit.RETROFIT)
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
    implementation(project(Modules.DATABASE))

    // Navigation
    implementation(Deps.AndroidX.Navigation.COMPOSE_NAVIGATION)
    implementation(Deps.AndroidX.Compose.RUNTIME)

    // Room
    api(Deps.AndroidX.Room.RUNTIME)
}

fun DependencyHandler.coreModelModuleDeps() {
    implementation(project(Modules.DESIGN))
    implementation(Deps.AndroidX.Compose.RUNTIME)
}

fun DependencyHandler.coreDesignModuleDeps() {
    // Compose
    implementation(Deps.AndroidX.Compose.MATERIAL)
    implementation(Deps.AndroidX.Compose.MATERIAL3)
    implementation(Deps.AndroidX.Compose.RUNTIME)
    implementation(Deps.AndroidX.Compose.ICONS)

    // Core
    implementation(Deps.AndroidX.CORE_KTX)
}

fun DependencyHandler.coreNavigationModuleDeps() {
    implementation(Deps.AndroidX.Navigation.NAVIGATION_UI)
    // Librairie
    implementation(project(Modules.DESIGN))

    // Compose
    implementation(Deps.AndroidX.Compose.ACTIVITY)
    implementation(Deps.AndroidX.Compose.RUNTIME)

    // Hilt
    implementation(Deps.Dagger.HILT_ANDROID)
    kapt(Deps.Dagger.HILT_ANDROID_COMPILER)

    // Navigation
    implementation(Deps.AndroidX.Navigation.NAVIGATION_UI)
    implementation(Deps.AndroidX.Navigation.COMPOSE_NAVIGATION)
}

fun DependencyHandler.featureMoviesModuleDeps() {
    // Librairie
    implementation(project(Modules.DATABASE))
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.NETWORK))

    // AndroidX
    implementation(Deps.AndroidX.Lifecycle.LIFECYCLE_RUNTIME)
    implementation(Deps.AndroidX.Kotlin.KOTLIN_COLLECTION)

    // Compose
    implementation(Deps.AndroidX.Compose.ACTIVITY)
    implementation(Deps.AndroidX.Compose.MATERIAL3)
    implementation(Deps.AndroidX.Compose.PREVIEW)
    implementation(Deps.AndroidX.Compose.RUNTIME)

    // Core
    implementation(Deps.AndroidX.CORE_KTX)

    // Coil
    implementation(Deps.Coil.COIL)
    implementation(Deps.Coil.COIL_COMPOSE)

    // Hilt
    implementation(Deps.Dagger.HILT_ANDROID)
    kapt(Deps.Dagger.HILT_ANDROID_COMPILER)

    // Retrofit
    implementation(Deps.Retrofit.RETROFIT)
    implementation(Deps.Retrofit.GSON)
    implementation(Deps.OkHttp.OK_HTTP)
    implementation(Deps.OkHttp.LOGGING)

    // Room
    api(Deps.AndroidX.Room.RUNTIME)
    kapt(Deps.AndroidX.Room.COMPILER)
    implementation(Deps.AndroidX.Room.ROOM_KTX)
}

fun DependencyHandler.coreDatabaseModuleDeps() {
    // Paging (For some reason, don't delete this line or you will have a Gradle issue)
    implementation(Deps.AndroidX.Paging.COMPOSE)

    // Room
    api(Deps.AndroidX.Room.RUNTIME)
    kapt(Deps.AndroidX.Room.COMPILER)
    implementation(Deps.AndroidX.Room.ROOM_KTX)
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
