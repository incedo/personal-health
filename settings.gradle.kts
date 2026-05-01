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
include(":core:coaches")
include(":core:designsystem")
include(":core:events")
include(":core:goals")
include(":core:health")
include(":core:media")
include(":core:newssocial")
include(":core:onboarding")
include(":core:recommendations")
include(":core:wellbeing")
include(":feature:home")
include(":feature:home-test")
include(":feature:onboarding")
include(":feature:onboarding-test")
include(":integration:app-usage")
include(":integration:health-connect")
include(":integration:samsung-health")
include(":integration:healthkit")
