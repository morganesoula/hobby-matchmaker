pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
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
