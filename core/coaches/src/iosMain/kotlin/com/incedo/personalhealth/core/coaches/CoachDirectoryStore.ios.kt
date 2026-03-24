package com.incedo.personalhealth.core.coaches

import platform.Foundation.NSUserDefaults

private const val COACH_DIRECTORY_KEY = "coach_directory"

actual object CoachDirectoryStore {
    actual fun coaches(): List<CoachProfile> = decodeCoachProfiles(
        NSUserDefaults.standardUserDefaults.stringForKey(COACH_DIRECTORY_KEY).orEmpty()
    )

    actual fun setCoaches(coaches: List<CoachProfile>) {
        NSUserDefaults.standardUserDefaults.setObject(
            encodeCoachProfiles(coaches),
            forKey = COACH_DIRECTORY_KEY
        )
    }
}
