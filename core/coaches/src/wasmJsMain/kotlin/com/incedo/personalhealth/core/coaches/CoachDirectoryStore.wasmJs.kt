package com.incedo.personalhealth.core.coaches

private const val COACH_DIRECTORY_KEY = "personal-health.coach-directory"

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun localStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun localStorageSetItem(key: String, value: String)

actual object CoachDirectoryStore {
    actual fun coaches(): List<CoachProfile> = decodeCoachProfiles(
        localStorageGetItem(COACH_DIRECTORY_KEY).orEmpty()
    )

    actual fun setCoaches(coaches: List<CoachProfile>) {
        localStorageSetItem(COACH_DIRECTORY_KEY, encodeCoachProfiles(coaches))
    }
}
