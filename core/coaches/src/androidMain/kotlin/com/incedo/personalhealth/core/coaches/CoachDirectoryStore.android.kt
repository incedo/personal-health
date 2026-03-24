package com.incedo.personalhealth.core.coaches

import java.io.File

actual object CoachDirectoryStore {
    private val preferenceFile = resolvePreferenceFile()

    actual fun coaches(): List<CoachProfile> = runCatching {
        decodeCoachProfiles(preferenceFile.takeIf { it.exists() }?.readText().orEmpty())
    }.getOrDefault(emptyList())

    actual fun setCoaches(coaches: List<CoachProfile>) {
        runCatching {
            preferenceFile.parentFile?.mkdirs()
            preferenceFile.writeText(encodeCoachProfiles(coaches))
        }
    }

    private fun resolvePreferenceFile(): File {
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()
        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/coach-directory.pref")
    }
}
