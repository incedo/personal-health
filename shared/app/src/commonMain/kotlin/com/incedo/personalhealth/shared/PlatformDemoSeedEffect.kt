package com.incedo.personalhealth.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.incedo.personalhealth.feature.home.ActivityTrackingSnapshot
import com.incedo.personalhealth.feature.home.FitnessActivitySession
import com.incedo.personalhealth.feature.home.NutritionLogEntry
import com.incedo.personalhealth.feature.home.PersistedActivityTrackingStore
import com.incedo.personalhealth.feature.home.PersistedFitnessActivityStore
import com.incedo.personalhealth.feature.home.PersistedNutritionLogStore

@Composable
internal fun PlatformDemoSeedEffect(
    fitnessActivityStore: PersistedFitnessActivityStore,
    activityTrackingStore: PersistedActivityTrackingStore,
    nutritionLogStore: PersistedNutritionLogStore,
    onSeeded: (Boolean, List<NutritionLogEntry>, List<FitnessActivitySession>, ActivityTrackingSnapshot) -> Unit
) {
    LaunchedEffect(Unit) {
        val seeded = seedPlatformDemoDataIfNeeded(
            fitnessActivityStore = fitnessActivityStore,
            activityTrackingStore = activityTrackingStore,
            nutritionLogStore = nutritionLogStore
        )
        onSeeded(
            seeded,
            nutritionLogStore.readEntries(),
            fitnessActivityStore.readSessions(),
            activityTrackingStore.readSnapshot()
        )
    }
}
