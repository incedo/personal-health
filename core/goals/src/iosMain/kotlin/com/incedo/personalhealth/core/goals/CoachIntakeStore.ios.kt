package com.incedo.personalhealth.core.goals

import platform.Foundation.NSUserDefaults

private const val COACH_INTAKE_KEY = "coach_intake"

actual object CoachIntakeStore {
    actual fun profile(): CoachIntakeProfile = decodeCoachIntake(
        NSUserDefaults.standardUserDefaults.stringForKey(COACH_INTAKE_KEY).orEmpty()
    )

    actual fun setProfile(profile: CoachIntakeProfile) {
        NSUserDefaults.standardUserDefaults.setObject(
            encodeCoachIntake(profile),
            forKey = COACH_INTAKE_KEY
        )
    }
}
