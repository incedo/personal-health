package com.incedo.personalhealth.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.events.SyncState
import com.incedo.personalhealth.core.health.HealthMetricType

@Composable
internal fun HealthSyncStatusCard(
    syncState: SyncState,
    channel: String,
    lastReadSummary: String
) {
    val bgColor = when (syncState) {
        SyncState.SYNCING -> MaterialTheme.colorScheme.primaryContainer
        SyncState.UP_TO_DATE -> MaterialTheme.colorScheme.secondaryContainer
        SyncState.ERROR -> MaterialTheme.colorScheme.errorContainer
        SyncState.IDLE -> MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Surface(
                color = bgColor,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Sync: ${syncState.name.lowercase()} ($channel)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
            Text(
                text = lastReadSummary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
internal fun HealthSyncStatsCard(
    strategy: HealthImportStrategy,
    intentReceivedCount: Int,
    intentSkippedCount: Int,
    intentAppliedCount: Int,
    intentFailedCount: Int,
    metricEventCounts: Map<HealthMetricType, Int>,
    onAction: (HealthImportActionId) -> Unit,
    onRecalculateDebug: (() -> Unit)?,
    latestUiMessage: String,
    importRequestCount: Int,
    importInProgress: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(strategy.title, style = MaterialTheme.typography.titleSmall)
            Text(
                "${strategy.platformName}: ${strategy.summary}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Nu zichtbaar: Withings krijgt voorrang voor gewicht, body composition en bloeddruk. Samsung/Health Connect blijven voor de overige Android data.",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "import clicks=$importRequestCount status=${if (importInProgress) "bezig" else "idle"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "received=$intentReceivedCount applied=$intentAppliedCount skipped=$intentSkippedCount failed=$intentFailedCount",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "steps=${metricEventCounts[HealthMetricType.STEPS] ?: 0} " +
                    "hr=${metricEventCounts[HealthMetricType.HEART_RATE_BPM] ?: 0} " +
                    "sleep=${metricEventCounts[HealthMetricType.SLEEP_DURATION_MINUTES] ?: 0} " +
                    "energy=${metricEventCounts[HealthMetricType.ACTIVE_ENERGY_KCAL] ?: 0} " +
                    "weight=${metricEventCounts[HealthMetricType.BODY_WEIGHT_KG] ?: 0}",
                style = MaterialTheme.typography.bodySmall
            )
            Text("Laatste melding: $latestUiMessage", style = MaterialTheme.typography.bodySmall)
            if (onRecalculateDebug != null) {
                Button(
                    onClick = onRecalculateDebug,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Recalculate debug")
                }
            }
            strategy.actions.forEach { action ->
                val importAction = action.id == HealthImportActionId.IMPORT_HISTORY
                Button(
                    onClick = { onAction(action.id) },
                    enabled = !importInProgress || !importAction,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        if (importAction && importInProgress) {
                            "Import bezig..."
                        } else {
                            action.label
                        }
                    )
                }
            }
        }
    }
}
