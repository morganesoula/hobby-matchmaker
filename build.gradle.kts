import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules
plugins {
    id(Plugins.ANDROID_APPLICATION) version (PluginVersion.AGP) apply false
    id(Plugins.ANDROIDX_NAVIGATION) version (PluginVersion.NAVIGATION) apply false
    id(Plugins.DAGGER_HILT) apply false
    id(Plugins.GOOGLE_SERVICES) version (PluginVersion.G_SERVICES) apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
    id("com.android.library") version "8.2.2" apply false
}

tasks {
    register("clean", Delete::class) {
        delete(layout.buildDirectory)
    }
}

tasks.withType(JavaCompile::class.java) {
    options.compilerArgs = mutableListOf("-jvmtoolchain", "1.17")
}
