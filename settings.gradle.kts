pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
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
include(":core:events")
include(":core:health")
include(":feature:home")
include(":feature:home-test")
include(":feature:onboarding")
include(":feature:onboarding-test")
include(":integration:health-connect")
include(":integration:healthkit")
