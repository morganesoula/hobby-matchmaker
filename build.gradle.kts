// Top-level build file where you can add configuration options common to all sub-projects/modules
plugins {
    id(Plugins.ANDROID_APPLICATION) version (PluginVersion.AGP) apply false
    //id(Plugins.ANDROID_LIBRARY) version (PluginVersion.AGP) apply false
    id(Plugins.AndroidxNavigation) version (PluginVersion.Navigation) apply false
    id(Plugins.DAGGER_HILT) apply false
    id(Plugins.GOOGLE_SERVICES) version (PluginVersion.GServices) apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
    id("com.android.library") version "8.2.2" apply false
}

tasks {
    register("clean", Delete::class) {
        delete(layout.buildDirectory)
    }
}