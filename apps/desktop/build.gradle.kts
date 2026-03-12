plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(19)
}

dependencies {
    implementation(project(":shared:app"))
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "com.incedo.personalhealth.desktop.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
