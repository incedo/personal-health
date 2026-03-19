package com.incedo.personalhealth.shared

actual object ProfilePreferenceStore {
    private var profileId: String? = null

    actual fun fitnessBodyProfileId(): String? = profileId

    actual fun setFitnessBodyProfileId(profileId: String) {
        this.profileId = profileId
    }
}
