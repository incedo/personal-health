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
        val home = System.getProperty("user.home").orEmpty().ifBlank { "." }
        return File(File(home), ".personal-health/onboarding.pref")
    }
}
