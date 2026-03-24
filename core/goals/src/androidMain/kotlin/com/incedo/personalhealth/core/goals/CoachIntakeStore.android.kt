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
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()
        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/coach-intake.pref")
    }
}
