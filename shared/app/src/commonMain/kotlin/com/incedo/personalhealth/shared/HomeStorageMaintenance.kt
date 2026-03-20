package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.home.ActivityTrackingStore
import com.incedo.personalhealth.feature.home.FitnessActivityStore

private const val HOME_STORAGE_SCHEMA_VERSION = "0.1.0-install-reset-activities"

internal fun runHomeStorageMaintenance(
    fitnessActivityStore: FitnessActivityStore,
    activityTrackingStore: ActivityTrackingStore
) {
    if (ProfilePreferenceStore.homeStorageSchemaVersion() == HOME_STORAGE_SCHEMA_VERSION) return
    fitnessActivityStore.clear()
    activityTrackingStore.clear()
    ProfilePreferenceStore.setHomeStorageSchemaVersion(HOME_STORAGE_SCHEMA_VERSION)
}
