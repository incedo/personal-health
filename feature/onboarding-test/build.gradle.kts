plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopTest by getting {
            dependencies {
                implementation(project(":feature:onboarding"))
                implementation(kotlin("test"))
            }
        }
    }
}
