package com.incedo.personalhealth.shared

expect object ProfilePreferenceStore {
    fun fitnessBodyProfileId(): String?
    fun setFitnessBodyProfileId(profileId: String)
}
