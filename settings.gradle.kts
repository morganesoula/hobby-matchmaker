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
include(":features")
include(":feature:movies")
include(":features:movies:data")
include(":features:movies:domain")
include(":features:movies:presentation")
include(":core:login")
include(":core:login:domain")
include(":core:login:presentation")
include(":core:session")
include(":core:session:data")
include(":core:session:domain")
include(":core:splashscreen")
include(":core:splashscreen:presentation")
include(":features:moviedetail")
include(":features:moviedetail:domain")
include(":features:moviedetail:data")
include(":features:moviedetail:presentation")
include(":core:database")
include(":core:database:dao")
include(":testUtils")
include(":testUtils:core")
include(":testUtils:feature")
include(":testUtils:common")
include(":shared")
