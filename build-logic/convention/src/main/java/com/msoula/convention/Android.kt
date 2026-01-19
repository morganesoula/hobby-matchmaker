package com.msoula.convention

import ProjectConfig
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureAndroidLibrary(project: Project) {
    val namespace = "com.msoula.hobbymatchmaker" + project.path
        .replace(":", ".")
        .replace("-", "")

    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
        this.namespace = namespace
        compileSdk = ProjectConfig.PROJECT_CONFIG_SDK_VERSION
        minSdk = ProjectConfig.PROJECT_CONFIG_MIN_SDK_VERSION
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
