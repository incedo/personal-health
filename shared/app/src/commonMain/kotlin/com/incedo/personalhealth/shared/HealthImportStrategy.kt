package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthMetricType

data class HealthImportStrategy(
    val platformName: String,
    val title: String,
    val summary: String,
    val actions: List<HealthImportAction>
)

data class HealthImportAction(
    val id: HealthImportActionId,
    val label: String
)

enum class HealthImportActionId {
    REQUEST_PERMISSIONS,
    IMPORT_HISTORY,
    OPEN_SOURCE_SETTINGS,
    START_LIVE_SYNC
}

expect fun currentHealthImportStrategy(): HealthImportStrategy

expect suspend fun executeHealthImportAction(
    actionId: HealthImportActionId,
    publishHealthEvent: suspend (HealthEvent) -> Unit,
    publishUiMessage: suspend (String) -> Unit
)

internal val DEFAULT_IMPORT_METRICS = setOf(
    HealthMetricType.STEPS,
    HealthMetricType.HEART_RATE_BPM,
    HealthMetricType.SLEEP_DURATION_MINUTES,
    HealthMetricType.ACTIVE_ENERGY_KCAL,
    HealthMetricType.BODY_WEIGHT_KG
)
