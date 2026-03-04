package com.msoula.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@DslMarker
annotation class MultiplatformDSL

@MultiplatformDSL
open class MultiplatformConfigExtension(
    private val project: Project,
    private val libs: VersionCatalog,
    private val compose: ComposePlugin.Dependencies? = null
) {
    var useCoil: Boolean = false

    fun useFirebase() {
        project.extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies {
                implementation(libs.findLibrary("firebase-kmp-auth").get())
                implementation(libs.findLibrary("firebase-kmp-firestore").get())
                implementation(libs.findLibrary("firebase-kmp-storage").get())
            }

            sourceSets.getByName("androidMain").dependencies {
                implementation(
                    project.dependencies.platform(
                        libs.findLibrary("firebase-bom").get()
                    )
                )
                implementation(libs.findLibrary("firebase-auth").get())
                implementation(libs.findLibrary("firebase-common").get())
                implementation(libs.findLibrary("firebase-firestore").get())
                implementation(libs.findLibrary("firebase-storage").get())
            }
        }
    }

    fun useCoil() {
        useCoil = true
        compose?.let { _ ->
            project.extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.findLibrary("coil-compose").get())
                }
            }
        }
    }
}
