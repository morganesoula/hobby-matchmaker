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
include(":core:database")
include(":core:design")
include(":core:di")
include(":core:model")
include(":core:navigation")
include(":core:network")
include(":feature")
include(":feature:movies")
