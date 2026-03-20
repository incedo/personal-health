package com.incedo.personalhealth.android

import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.integration.healthconnect.HealthConnectGateway

internal fun buildImportCompletionMessage(
    samsungCount: Int,
    healthConnectCount: Int,
    pendingHealthConnectMetrics: Set<HealthMetricType>,
    healthConnectGateway: HealthConnectGateway?
): String = when {
    samsungCount > 0 && healthConnectCount > 0 ->
        "Import klaar: Samsung Health $samsungCount records, Health Connect $healthConnectCount records."
    samsungCount > 0 && pendingHealthConnectMetrics.isNotEmpty() && healthConnectGateway == null ->
        "Samsung Health import klaar: $samsungCount records. Health Connect blijft fallback voor ${pendingHealthConnectMetrics.joinToString()}."
    samsungCount > 0 ->
        "Samsung Health import klaar: $samsungCount records."
    healthConnectCount > 0 ->
        "Health Connect import klaar: $healthConnectCount records."
    else -> "Geen nieuwe health records gevonden."
}
