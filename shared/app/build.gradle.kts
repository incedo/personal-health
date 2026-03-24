plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    id("org.jetbrains.kotlin.native.cocoapods")
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    jvm("desktop")
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
    }

    cocoapods {
        version = "1.0.0"
        summary = "Shared Compose app module for Personal Health"
        homepage = "https://github.com/incedo/personal-health"
        ios.deploymentTarget = "15.0"

        framework {
            baseName = "SharedApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:events"))
            implementation(project(":core:goals"))
            implementation(project(":core:onboarding"))
            implementation(project(":core:recommendations"))
            implementation(project(":core:health"))
            implementation(project(":core:wellbeing"))
            implementation(project(":feature:home"))
            implementation(project(":feature:onboarding"))
            implementation(project(":core:designsystem"))
            implementation(libs.kotlinx.serialization.json)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
        androidMain.dependencies {
            implementation(project(":integration:health-connect"))
        }
        iosMain.dependencies {
            implementation(project(":integration:healthkit"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.incedo.personalhealth.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
