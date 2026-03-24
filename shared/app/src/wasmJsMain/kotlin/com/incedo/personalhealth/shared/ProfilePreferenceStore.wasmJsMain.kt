package com.incedo.personalhealth.shared

actual object ProfilePreferenceStore {
    private var profileId: String? = null
    private var socialAppPackageIds: Set<String>? = null
    private var storageSchemaVersion: String? = null

    actual fun fitnessBodyProfileId(): String? = profileId

    actual fun setFitnessBodyProfileId(profileId: String) {
        this.profileId = profileId
    }

    actual fun selectedSocialAppPackageIds(): Set<String>? = socialAppPackageIds

    actual fun setSelectedSocialAppPackageIds(packageIds: Set<String>) {
        socialAppPackageIds = packageIds
    }

    actual fun homeStorageSchemaVersion(): String? = storageSchemaVersion

    actual fun setHomeStorageSchemaVersion(version: String) {
        storageSchemaVersion = version
    }
}
