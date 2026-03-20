package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.home.PersistedActivityTrackingStore
import com.incedo.personalhealth.feature.home.PersistedFitnessActivityStore
import com.incedo.personalhealth.feature.home.PersistedNutritionLogStore
import com.incedo.personalhealth.feature.home.QuickActivityType

private const val WEB_DEMO_SEED_KEY = "personal-health.web-demo-seeded.v1"
private const val WEB_IMPORT_STORAGE_KEY = "personal-health.canonical-import.v1"

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun demoSeedStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun demoSeedStorageSetItem(key: String, value: String)

@JsFun("() => Date.now()")
private external fun webNowEpochMillis(): Double

@JsFun("() => { const d = new Date(); d.setHours(0, 0, 0, 0); return d.getTime(); }")
private external fun webStartOfDayEpochMillis(): Double

actual suspend fun seedPlatformDemoDataIfNeeded(
    fitnessActivityStore: PersistedFitnessActivityStore,
    activityTrackingStore: PersistedActivityTrackingStore,
    nutritionLogStore: PersistedNutritionLogStore
): Boolean {
    if (demoSeedStorageGetItem(WEB_DEMO_SEED_KEY) == "true") return false

    val nowEpochMillis = webNowEpochMillis().toLong()
    val dayStartEpochMillis = webStartOfDayEpochMillis().toLong()

    if (fitnessActivityStore.readSessions().isEmpty()) {
        demoFitnessSessions(dayStartEpochMillis).forEach(fitnessActivityStore::upsertSession)
    }

    if (activityTrackingStore.readSnapshot().completedSessions.isEmpty() &&
        activityTrackingStore.readSnapshot().activeSession == null
    ) {
        activityTrackingStore.startActivity(QuickActivityType.WALKING, dayStartEpochMillis + 7L * HOUR_MILLIS)
        activityTrackingStore.stopActiveActivity(dayStartEpochMillis + 7L * HOUR_MILLIS + 35L * MINUTE_MILLIS)
        activityTrackingStore.startActivity(QuickActivityType.RUNNING, dayStartEpochMillis + 12L * HOUR_MILLIS)
        activityTrackingStore.stopActiveActivity(dayStartEpochMillis + 12L * HOUR_MILLIS + 42L * MINUTE_MILLIS)
        activityTrackingStore.startActivity(QuickActivityType.CYCLING, nowEpochMillis - 18L * MINUTE_MILLIS)
    }

    if (nutritionLogStore.readEntries().isEmpty()) {
        demoNutritionEntries(dayStartEpochMillis).forEach(nutritionLogStore::addEntry)
    }

    if (demoSeedStorageGetItem(WEB_IMPORT_STORAGE_KEY).isNullOrBlank()) {
        demoSeedStorageSetItem(
            WEB_IMPORT_STORAGE_KEY,
            webDemoCanonicalImportPayload(dayStartEpochMillis, nowEpochMillis)
        )
    }

    if (!OnboardingPreferenceStore.isCompleted()) {
        OnboardingPreferenceStore.setSelectedGoalId("Activity")
        OnboardingPreferenceStore.setStepIndex(2)
        OnboardingPreferenceStore.setCompleted(true)
    }

    demoSeedStorageSetItem(WEB_DEMO_SEED_KEY, "true")
    return true
}

private const val MINUTE_MILLIS = 60_000L
private const val HOUR_MILLIS = 60L * MINUTE_MILLIS
