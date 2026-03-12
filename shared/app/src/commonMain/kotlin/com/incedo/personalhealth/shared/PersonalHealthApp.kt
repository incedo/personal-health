package com.incedo.personalhealth.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PersonalHealthTheme
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.events.SyncState
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.feature.home.HomeScreen
import com.incedo.personalhealth.feature.onboarding.OnboardingRoute
import kotlinx.coroutines.launch

@Composable
fun PersonalHealthApp() {
    val appScope = rememberCoroutineScope()
    var onboardingComplete by remember { mutableStateOf(OnboardingPreferenceStore.isCompleted()) }
    var healthSyncState by remember { mutableStateOf(SyncState.IDLE) }
    var healthSyncChannel by remember { mutableStateOf("health-history-import") }
    var lastReadSummary by remember { mutableStateOf("Nog geen health records gelezen") }
    var latestUiMessage by remember { mutableStateOf("Nog geen acties uitgevoerd") }
    var importRequestCount by remember { mutableStateOf(0) }
    var intentReceivedCount by remember { mutableStateOf(0) }
    var intentSkippedCount by remember { mutableStateOf(0) }
    var intentAppliedCount by remember { mutableStateOf(0) }
    var intentFailedCount by remember { mutableStateOf(0) }
    var metricEventCounts by remember {
        mutableStateOf(
            mapOf(
                HealthMetricType.STEPS to 0,
                HealthMetricType.HEART_RATE_BPM to 0,
                HealthMetricType.SLEEP_DURATION_MINUTES to 0,
                HealthMetricType.ACTIVE_ENERGY_KCAL to 0,
                HealthMetricType.BODY_WEIGHT_KG to 0
            )
        )
    }

    LaunchedEffect(Unit) {
        AppBus.events.events.collect { event ->
            when (event) {
                is FrontendEvent.SyncStateChanged -> {
                    if (event.channel.startsWith("health-history-import")) {
                        healthSyncState = event.state
                        healthSyncChannel = event.channel
                    }
                }

                is FrontendEvent.UiFeedbackRequested -> {
                    latestUiMessage = event.message
                }

                is HealthEvent.RecordsRead -> {
                    val sourceLabel = event.source.name.lowercase()
                    lastReadSummary = "$sourceLabel: ${event.count} records"
                }

                is HealthEvent.SyncRequested -> {
                    importRequestCount += 1
                    latestUiMessage = "Importverzoek verstuurd (${event.metrics.size} metrics)"
                }

                is HealthEvent.LiveSyncIntentReceived -> {
                    intentReceivedCount += 1
                }

                is HealthEvent.LiveSyncIntentSkippedDuplicate -> {
                    intentSkippedCount += 1
                }

                is HealthEvent.LiveSyncIntentApplied -> {
                    intentAppliedCount += 1
                    val sourceLabel = event.source.name.lowercase()
                    val triggerLabel = event.trigger.name.lowercase()
                    lastReadSummary = "$sourceLabel/$triggerLabel: ${event.count} changed records"
                }

                is HealthEvent.LiveSyncIntentFailed -> {
                    intentFailedCount += 1
                    lastReadSummary = "intent failed: ${event.reason}"
                }

                is HealthEvent.LiveSyncStepsIntentApplied -> {
                    metricEventCounts = metricEventCounts.bump(HealthMetricType.STEPS)
                }

                is HealthEvent.LiveSyncHeartRateIntentApplied -> {
                    metricEventCounts = metricEventCounts.bump(HealthMetricType.HEART_RATE_BPM)
                }

                is HealthEvent.LiveSyncSleepIntentApplied -> {
                    metricEventCounts = metricEventCounts.bump(HealthMetricType.SLEEP_DURATION_MINUTES)
                }

                is HealthEvent.LiveSyncActiveEnergyIntentApplied -> {
                    metricEventCounts = metricEventCounts.bump(HealthMetricType.ACTIVE_ENERGY_KCAL)
                }

                is HealthEvent.LiveSyncBodyWeightIntentApplied -> {
                    metricEventCounts = metricEventCounts.bump(HealthMetricType.BODY_WEIGHT_KG)
                }
            }
        }
    }

    val derivedSteps = 4500 + (metricEventCounts[HealthMetricType.STEPS] ?: 0) * 250
    val derivedHeartRate = (68 - (metricEventCounts[HealthMetricType.HEART_RATE_BPM] ?: 0)).coerceIn(52, 90)
    val fitScore = (35 + (derivedSteps / 180) - ((derivedHeartRate - 60).coerceAtLeast(0) / 2)).coerceIn(0, 100)

    PersonalHealthTheme {
        if (onboardingComplete) {
            HomeScreen(
                fitScore = fitScore,
                steps = derivedSteps,
                heartRateBpm = derivedHeartRate,
                profileName = "Kees",
                syncContent = {
                    HealthSyncStatusCard(
                        syncState = healthSyncState,
                        channel = healthSyncChannel,
                        lastReadSummary = lastReadSummary
                    )
                    HealthSyncStatsCard(
                        intentReceivedCount = intentReceivedCount,
                        intentSkippedCount = intentSkippedCount,
                        intentAppliedCount = intentAppliedCount,
                        intentFailedCount = intentFailedCount,
                        metricEventCounts = metricEventCounts,
                        onRequestHistoryImport = {
                            appScope.launch {
                                AppBus.events.publish(
                                    HealthEvent.SyncRequested(
                                        metrics = setOf(
                                            HealthMetricType.STEPS,
                                            HealthMetricType.HEART_RATE_BPM,
                                            HealthMetricType.SLEEP_DURATION_MINUTES,
                                            HealthMetricType.ACTIVE_ENERGY_KCAL,
                                            HealthMetricType.BODY_WEIGHT_KG
                                        ),
                                        emittedAtEpochMillis = System.currentTimeMillis()
                                    )
                                )
                            }
                        },
                        onRequestPermissions = {
                            appScope.launch {
                                AppBus.events.publish(
                                    HealthEvent.PermissionsRequested(
                                        emittedAtEpochMillis = System.currentTimeMillis()
                                    )
                                )
                            }
                        },
                        onOpenHealthConnectSettings = {
                            appScope.launch {
                                AppBus.events.publish(
                                    HealthEvent.HealthConnectSettingsRequested(
                                        emittedAtEpochMillis = System.currentTimeMillis()
                                    )
                                )
                            }
                        },
                        latestUiMessage = latestUiMessage,
                        importRequestCount = importRequestCount,
                        importInProgress = healthSyncState == SyncState.SYNCING
                    )
                },
                profileContent = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Laatste status", style = MaterialTheme.typography.titleSmall)
                            Text(latestUiMessage, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            )
        } else {
            OnboardingRoute(
                onFinished = {
                    onboardingComplete = true
                    OnboardingPreferenceStore.setCompleted(true)
                }
            )
        }
    }
}

@Composable
private fun HealthSyncStatusCard(
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
private fun HealthSyncStatsCard(
    intentReceivedCount: Int,
    intentSkippedCount: Int,
    intentAppliedCount: Int,
    intentFailedCount: Int,
    metricEventCounts: Map<HealthMetricType, Int>,
    onRequestHistoryImport: () -> Unit,
    onRequestPermissions: () -> Unit,
    onOpenHealthConnectSettings: () -> Unit,
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
            Text("Health Sync", style = MaterialTheme.typography.titleSmall)
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
            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Geef permissies")
            }
            Button(
                onClick = onOpenHealthConnectSettings,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Open Health Connect")
            }
            Button(
                onClick = onRequestHistoryImport,
                enabled = !importInProgress,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(if (importInProgress) "Import bezig..." else "Importeer historie")
            }
        }
    }
}

private fun Map<HealthMetricType, Int>.bump(metric: HealthMetricType): Map<HealthMetricType, Int> {
    val updated = toMutableMap()
    updated[metric] = (updated[metric] ?: 0) + 1
    return updated
}
