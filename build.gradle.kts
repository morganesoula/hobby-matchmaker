// Top-level build file where you can add configuration options common to all sub-projects/modules
plugins {
    id(Plugins.ANDROID_APPLICATION) version (PluginVersion.AGP) apply false
    id(Plugins.ANDROID_LIBRARY) version (PluginVersion.AGP) apply false
    id(Plugins.AndroidxNavigation) version (PluginVersion.Navigation) apply false
    id(Plugins.DAGGER_HILT) apply false
    id(Plugins.GOOGLE_SERVICES) version (PluginVersion.GServices) apply false
}

tasks {
    register("clean", Delete::class) {
        delete(layout.buildDirectory)
    }
}