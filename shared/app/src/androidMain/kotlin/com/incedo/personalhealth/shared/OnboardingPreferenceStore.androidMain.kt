package com.incedo.personalhealth.shared

import java.io.File

actual object OnboardingPreferenceStore {
    private const val COMPLETED = "completed"
    private val preferenceFile = resolvePreferenceFile()

    actual fun isCompleted(): Boolean = runCatching {
        preferenceFile.takeIf { it.exists() }?.readText()?.trim() == COMPLETED
    }.getOrDefault(false)

    actual fun setCompleted(completed: Boolean) {
        runCatching {
            preferenceFile.parentFile?.mkdirs()
            preferenceFile.writeText(if (completed) COMPLETED else "")
        }
    }

    private fun resolvePreferenceFile(): File {
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()

        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/onboarding.pref")
    }
}
