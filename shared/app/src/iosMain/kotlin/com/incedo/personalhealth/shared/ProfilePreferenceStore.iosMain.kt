package com.incedo.personalhealth.shared

import platform.Foundation.NSUserDefaults

actual object ProfilePreferenceStore {
    private const val BODY_PROFILE_KEY = "fitness_body_profile_id"
    private const val SOCIAL_APPS_KEY = "selected_social_app_packages"
    private const val SCHEMA_VERSION_KEY = "home_storage_schema_version"

    actual fun fitnessBodyProfileId(): String? =
        NSUserDefaults.standardUserDefaults.stringForKey(BODY_PROFILE_KEY)

    actual fun setFitnessBodyProfileId(profileId: String) {
        NSUserDefaults.standardUserDefaults.setObject(profileId, forKey = BODY_PROFILE_KEY)
    }

    actual fun selectedSocialAppPackageIds(): Set<String>? =
        NSUserDefaults.standardUserDefaults.stringForKey(SOCIAL_APPS_KEY)
            ?.split("\n")
            ?.map(String::trim)
            ?.filter(String::isNotBlank)
            ?.toSet()

    actual fun setSelectedSocialAppPackageIds(packageIds: Set<String>) {
        NSUserDefaults.standardUserDefaults.setObject(
            packageIds.sorted().joinToString(separator = "\n"),
            forKey = SOCIAL_APPS_KEY
        )
    }

    actual fun homeStorageSchemaVersion(): String? =
        NSUserDefaults.standardUserDefaults.stringForKey(SCHEMA_VERSION_KEY)

    actual fun setHomeStorageSchemaVersion(version: String) {
        NSUserDefaults.standardUserDefaults.setObject(version, forKey = SCHEMA_VERSION_KEY)
    }
}
