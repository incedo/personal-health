plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.paparazzi) apply false
    alias(libs.plugins.kover)
}

buildscript {
    dependencies {
        classpath("com.karumi:shot:6.1.0")
    }
}

val maxSourceFileLines = 300

dependencies {
    kover(project(":apps:android"))
    kover(project(":apps:desktop"))
    kover(project(":apps:web"))
    kover(project(":shared:app"))
    kover(project(":core:designsystem"))
    kover(project(":core:events"))
    kover(project(":core:health"))
    kover(project(":feature:home"))
    kover(project(":feature:home-test"))
    kover(project(":feature:onboarding"))
    kover(project(":feature:onboarding-test"))
    kover(project(":integration:health-connect"))
    kover(project(":integration:healthkit"))
}

tasks.register("qualityGateBase") {
    group = "verification"
    description = "Runs non-visual quality gate checks (CI-safe)."
    dependsOn(
        "lineCountCheck",
        ":apps:desktop:test",
        ":apps:web:compileKotlinWasmJs",
        ":shared:app:compileKotlinDesktop",
        ":feature:home-test:desktopTest",
        ":feature:onboarding-test:desktopTest",
        ":core:events:compileKotlinDesktop",
        ":core:health:compileKotlinDesktop",
        ":integration:health-connect:compileDebugKotlin",
        ":apps:android:lintDebug",
        "koverXmlReport",
        "koverVerify",
        "coverageLayerGate"
    )
}

tasks.register("qualityGate") {
    group = "verification"
    description = "Alias for base quality checks. Use scripts/quality-gate-local.sh for optional visual checks."
    dependsOn("qualityGateBase")
}

tasks.register("lineCountCheck") {
    group = "verification"
    description = "Fails when Kotlin and Gradle source files exceed 300 lines unless explicitly baselined."

    doLast {
        val baselineFile = rootProject.file("config/max-file-lines-baseline.txt")
        require(baselineFile.exists()) {
            "Missing baseline file at ${baselineFile.absolutePath}"
        }

        val baseline = baselineFile.readLines()
            .map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .associate { line ->
                val parts = line.split("=")
                require(parts.size == 2) { "Invalid baseline entry: $line" }
                parts[0] to parts[1].toInt()
            }

        val oversizedFiles = fileTree(rootDir) {
            include("**/*.kt", "**/*.kts")
            exclude(".gradle/**", "**/build/**")
        }.files.map { file ->
            val relativePath = file.relativeTo(rootDir).invariantSeparatorsPath
            relativePath to file.useLines { it.count() }
        }.filter { (_, lineCount) ->
            lineCount > maxSourceFileLines
        }.sortedBy { it.first }

        val failures = oversizedFiles.mapNotNull { (path, currentLineCount) ->
            val baselineCount = baseline[path]
            when {
                baselineCount == null ->
                    "New oversized file: $path has $currentLineCount lines (max $maxSourceFileLines)"
                currentLineCount > baselineCount ->
                    "Legacy oversized file grew: $path has $currentLineCount lines (baseline $baselineCount)"
                else -> null
            }
        }

        if (failures.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("lineCountCheck failed:")
                    failures.forEach { appendLine("- $it") }
                }
            )
        }
    }
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn("lineCountCheck")
}

tasks.register("coverageLayerGate") {
    group = "verification"
    description = "Validates coverage targets for core and feature layers from root Kover report."
    dependsOn("koverXmlReport")

    doLast {
        val reportFile = layout.buildDirectory.file("reports/kover/report.xml").get().asFile
        require(reportFile.exists()) {
            "Kover report not found at ${reportFile.absolutePath}. Run koverXmlReport first."
        }

        val packagePattern = Regex("""<package name="([^"]+)">""")
        val lineCounterPattern = Regex("""<counter type="LINE" missed="(\d+)" covered="(\d+)"/>""")

        val packageCounters = mutableMapOf<String, Pair<Int, Int>>()
        var currentPackage: String? = null
        var lastLineCounter: Pair<Int, Int>? = null

        reportFile.forEachLine { line ->
            packagePattern.find(line)?.let { match ->
                currentPackage = match.groupValues[1]
                lastLineCounter = null
            }
            lineCounterPattern.find(line)?.let { match ->
                lastLineCounter = match.groupValues[1].toInt() to match.groupValues[2].toInt()
            }
            if (line.contains("</package>") && currentPackage != null && lastLineCounter != null) {
                packageCounters[currentPackage!!] = lastLineCounter!!
                currentPackage = null
                lastLineCounter = null
            }
        }

        fun coverageFor(prefixes: List<String>): Triple<Int, Int, Double> {
            var missed = 0
            var covered = 0
            packageCounters.forEach { (pkg, counter) ->
                if (prefixes.any { pkg.startsWith(it) }) {
                    missed += counter.first
                    covered += counter.second
                }
            }
            val total = missed + covered
            val percent = if (total == 0) 0.0 else (covered.toDouble() / total.toDouble()) * 100.0
            return Triple(missed, covered, percent)
        }

        val (coreMissed, coreCovered, corePct) = coverageFor(
            listOf(
                "com/incedo/personalhealth/core/designsystem",
                "com/incedo/personalhealth/core/events",
                "com/incedo/personalhealth/core/health"
            )
        )
        val (featureMissed, featureCovered, featurePct) = coverageFor(
            listOf(
                "com/incedo/personalhealth/feature/home",
                "com/incedo/personalhealth/feature/onboarding"
            )
        )

        val coreMin = 90.0
        val featureMin = 80.0

        logger.lifecycle(
            "Coverage layer gate: core=${"%.2f".format(corePct)}% ($coreCovered/${
                coreCovered + coreMissed
            }), feature=${"%.2f".format(featurePct)}% ($featureCovered/${featureCovered + featureMissed})"
        )

        if (corePct < coreMin) {
            throw GradleException(
                "Core coverage gate failed: ${"%.2f".format(corePct)}% < ${"%.2f".format(coreMin)}%"
            )
        }
        if (featurePct < featureMin) {
            throw GradleException(
                "Feature coverage gate failed: ${"%.2f".format(featurePct)}% < ${"%.2f".format(featureMin)}%"
            )
        }
    }
}

subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        apply(plugin = "org.jetbrains.kotlinx.kover")
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jetbrains.kotlinx.kover")
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.android") {
        apply(plugin = "org.jetbrains.kotlinx.kover")
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.BuildConfig",
                    "*.R",
                    "*.R$*",
                    "*.Manifest*",
                    "com.incedo.personalhealth.android.*",
                    "com.incedo.personalhealth.desktop.*",
                    "com.incedo.personalhealth.web.*",
                    "com.incedo.personalhealth.integration.healthconnect.*",
                    "com.incedo.personalhealth.integration.healthkit.*",
                    "com.incedo.personalhealth.shared.PersonalHealthAppKt*",
                    "com.incedo.personalhealth.shared.MainViewControllerKt*",
                    "com.incedo.personalhealth.shared.IOSSharedUiBridge*",
                    "com.incedo.personalhealth.shared.OnboardingPreferenceStore*",
                    "com.incedo.personalhealth.feature.home.HomeScreenKt*",
                    "com.incedo.personalhealth.feature.onboarding.OnboardingScreenKt*",
                    "com.incedo.personalhealth.feature.onboarding.OnboardingUiStateSaver*",
                    "*ComposableSingletons*"
                )
            }
        }
        verify {
            rule {
                bound {
                    minValue = 70
                }
            }
        }
    }
}
