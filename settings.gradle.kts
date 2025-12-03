pluginManagement {
    repositories {
        includeBuild("build-logic")
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    plugins {
        id("com.codingfeline.buildkonfig") version "0.17.1"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "HobbyMatchmaker"
include(":composeApp")
include(":core")
include(":core:authentication")
include(":core:authentication:data")
include(":core:authentication:domain")
include(":core:common")
include(":core:database")
include(":core:design")
include(":core:login")
include(":core:login:domain")
include(":core:login:presentation")
include(":core:navigation")
include(":core:navigation:presentation")
include(":core:session")
include(":core:session:data")
include(":core:session:domain")
include(":core:splashscreen")
include(":core:splashscreen:presentation")
include(":core:network")
include(":features")
include(":features:moviedetail")
include(":features:moviedetail:data")
include(":features:moviedetail:domain")
include(":features:moviedetail:presentation")
include(":features:movies:data")
include(":features:movies:domain")
include(":features:movies:presentation")
include(":features:profile")
include(":features:profile:data")
include(":features:profile:domain")
include(":features:profile:presentation")
include(":features:social")
include(":features:social:data")
include(":features:social:domain")
include(":features:social:presentation")
