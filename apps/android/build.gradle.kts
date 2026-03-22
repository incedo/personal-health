plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.paparazzi)
}

apply(plugin = "shot")

android {
    namespace = "com.incedo.personalhealth.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.incedo.personalhealth.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    implementation(project(":feature:home"))
    implementation(project(":shared:app"))
    implementation(project(":core:events"))
    implementation(project(":core:health"))
    implementation(project(":core:wellbeing"))
    implementation(project(":integration:app-usage"))
    implementation(project(":integration:health-connect"))
    implementation(project(":integration:samsung-health"))
    implementation(files(rootProject.file("integration/samsung-health/libs/samsung-health-data-api-1.1.0.aar")))
    implementation(libs.kotlin.parcelize.runtime)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.health.connect.client)
    implementation(compose.ui)
    implementation(compose.material3)
    debugImplementation(compose.uiTooling)

    testImplementation(kotlin("test"))
    testImplementation(libs.paparazzi)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
