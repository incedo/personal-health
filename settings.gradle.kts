pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
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

rootProject.name = "personal-health"

include(":apps:android")
include(":apps:desktop")
include(":apps:web")
include(":shared:app")
include(":core:designsystem")
include(":feature:home")
