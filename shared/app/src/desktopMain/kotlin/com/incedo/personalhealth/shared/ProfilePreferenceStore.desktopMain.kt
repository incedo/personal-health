package com.incedo.personalhealth.shared

import java.io.File

actual object ProfilePreferenceStore {
    private val preferenceFile = resolvePreferenceFile()

    actual fun fitnessBodyProfileId(): String? = runCatching {
        preferenceFile.takeIf { it.exists() }?.readText()?.trim()?.takeIf { it.isNotBlank() }
    }.getOrNull()

    actual fun setFitnessBodyProfileId(profileId: String) {
        runCatching {
            preferenceFile.parentFile?.mkdirs()
            preferenceFile.writeText(profileId)
        }
    }

    private fun resolvePreferenceFile(): File {
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()

        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/profile-body.pref")
    }
}
