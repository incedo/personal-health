package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.home.PersistedActivityTrackingStore
import com.incedo.personalhealth.feature.home.PersistedFitnessActivityStore
import com.incedo.personalhealth.feature.home.PersistedNutritionLogStore

expect suspend fun seedPlatformDemoDataIfNeeded(
    fitnessActivityStore: PersistedFitnessActivityStore,
    activityTrackingStore: PersistedActivityTrackingStore,
    nutritionLogStore: PersistedNutritionLogStore
): Boolean
