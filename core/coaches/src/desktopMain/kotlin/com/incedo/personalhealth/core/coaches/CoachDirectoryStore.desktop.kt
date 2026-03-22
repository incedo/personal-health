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
        val home = System.getProperty("user.home").orEmpty().ifBlank { "." }
        return File(File(home), ".personal-health/coach-directory.pref")
    }
}
