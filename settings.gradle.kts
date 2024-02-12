pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "androidx.navigation" -> {
                    useModule("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
                }
                "dagger.hilt.android.plugin" -> {
                    useModule("com.google.dagger:hilt-android-gradle-plugin:2.46")
                }
            }
        }
    }

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Hobby Matchmaker"
include(":app")
include(":auth")
include(":core")
include(":core:network")
include(":core:model")
include(":core:di")
include(":core:design")
include(":core:navigation")
