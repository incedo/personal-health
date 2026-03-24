package com.incedo.personalhealth.core.goals

import java.io.File

actual object CoachIntakeStore {
    private val preferenceFile = resolvePreferenceFile()

    actual fun profile(): CoachIntakeProfile = runCatching {
        decodeCoachIntake(preferenceFile.takeIf { it.exists() }?.readText().orEmpty())
    }.getOrDefault(CoachIntakeProfile())

    actual fun setProfile(profile: CoachIntakeProfile) {
        runCatching {
            preferenceFile.parentFile?.mkdirs()
            preferenceFile.writeText(encodeCoachIntake(profile))
        }
    }

    private fun resolvePreferenceFile(): File {
        val home = System.getProperty("user.home").orEmpty().ifBlank { "." }
        return File(File(home), ".personal-health/coach-intake.pref")
    }
}
