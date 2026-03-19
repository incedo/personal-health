package com.incedo.personalhealth.shared

import platform.Foundation.NSUserDefaults

actual object ProfilePreferenceStore {
    private const val KEY = "fitness_body_profile_id"

    actual fun fitnessBodyProfileId(): String? =
        NSUserDefaults.standardUserDefaults.stringForKey(KEY)

    actual fun setFitnessBodyProfileId(profileId: String) {
        NSUserDefaults.standardUserDefaults.setObject(profileId, forKey = KEY)
    }
}
