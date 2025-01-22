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
include(":core")
include(":core:authentication")
include(":core:authentication:data")
include(":core:authentication:domain")
include(":core:common")
include(":core:design")
include(":core:di")
include(":core:navigation")
include(":core:network")
include(":feature")
include(":feature:movies")
include(":feature:movies:data")
include(":feature:movies:domain")
include(":feature:movies:presentation")
include(":core:login")
include(":core:login:domain")
include(":core:login:presentation")
include(":core:session")
include(":core:session:data")
include(":core:session:domain")
include(":core:splashscreen")
include(":core:splashscreen:presentation")
include(":feature:moviedetail")
include(":feature:moviedetail:domain")
include(":feature:moviedetail:data")
include(":feature:moviedetail:presentation")
include(":core:database")
include(":core:database:dao")
include(":testUtils")
include(":testUtils:core")
include(":testUtils:feature")
include(":testUtils:common")
include(":shared")
