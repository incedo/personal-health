package com.incedo.personalhealth.shared

expect object ProfilePreferenceStore {
    fun fitnessBodyProfileId(): String?
    fun setFitnessBodyProfileId(profileId: String)
    fun homeStorageSchemaVersion(): String?
    fun setHomeStorageSchemaVersion(version: String)
}
