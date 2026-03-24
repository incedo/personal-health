package com.incedo.personalhealth.core.goals

private const val COACH_INTAKE_KEY = "personal-health.coach-intake"

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun localStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun localStorageSetItem(key: String, value: String)

actual object CoachIntakeStore {
    actual fun profile(): CoachIntakeProfile = decodeCoachIntake(
        localStorageGetItem(COACH_INTAKE_KEY).orEmpty()
    )

    actual fun setProfile(profile: CoachIntakeProfile) {
        localStorageSetItem(COACH_INTAKE_KEY, encodeCoachIntake(profile))
    }
}
