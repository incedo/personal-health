package com.incedo.personalhealth.shared

import platform.Foundation.NSUserDefaults

actual object ProfilePreferenceStore {
    private const val BODY_PROFILE_KEY = "fitness_body_profile_id"
    private const val SCHEMA_VERSION_KEY = "home_storage_schema_version"

    actual fun fitnessBodyProfileId(): String? =
        NSUserDefaults.standardUserDefaults.stringForKey(BODY_PROFILE_KEY)

    actual fun setFitnessBodyProfileId(profileId: String) {
        NSUserDefaults.standardUserDefaults.setObject(profileId, forKey = BODY_PROFILE_KEY)
    }

    actual fun homeStorageSchemaVersion(): String? =
        NSUserDefaults.standardUserDefaults.stringForKey(SCHEMA_VERSION_KEY)

    actual fun setHomeStorageSchemaVersion(version: String) {
        NSUserDefaults.standardUserDefaults.setObject(version, forKey = SCHEMA_VERSION_KEY)
    }
}
