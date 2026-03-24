package com.incedo.personalhealth.shared

expect object ProfilePreferenceStore {
    fun fitnessBodyProfileId(): String?
    fun setFitnessBodyProfileId(profileId: String)
    fun selectedSocialAppPackageIds(): Set<String>?
    fun setSelectedSocialAppPackageIds(packageIds: Set<String>)
    fun homeStorageSchemaVersion(): String?
    fun setHomeStorageSchemaVersion(version: String)
}
