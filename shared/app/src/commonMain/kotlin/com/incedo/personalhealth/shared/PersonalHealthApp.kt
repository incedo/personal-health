package com.incedo.personalhealth.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PersonalHealthTheme
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.events.SyncState
import com.incedo.personalhealth.core.health.CanonicalHealthImportDocument
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.buildTodayStepsSnapshot
import com.incedo.personalhealth.core.health.currentEpochMillis
import com.incedo.personalhealth.feature.home.HomeDetailDestination
import com.incedo.personalhealth.feature.home.HomeScreen
import com.incedo.personalhealth.feature.home.FitnessBodyProfile
import com.incedo.personalhealth.feature.home.PersistedFitnessActivityStore
import com.incedo.personalhealth.feature.home.PlatformFitnessActivityPersistenceDriver
import com.incedo.personalhealth.feature.home.QuickActivityEntry
import com.incedo.personalhealth.feature.home.QuickActivityType
import com.incedo.personalhealth.feature.home.StepTimelinePoint
import com.incedo.personalhealth.feature.home.HomeThemeMode
import com.incedo.personalhealth.feature.home.fallbackStepTimeline
import com.incedo.personalhealth.feature.home.fitnessSessionToQuickActivityEntry
import com.incedo.personalhealth.feature.home.logQuickActivity
import com.incedo.personalhealth.feature.home.summarizeStepTimeline
import com.incedo.personalhealth.feature.onboarding.OnboardingRoute
import kotlinx.coroutines.launch

@Composable
fun PersonalHealthApp() {
    val appScope = rememberCoroutineScope()
    val importStrategy = remember { currentHealthImportStrategy() }
    val fitnessActivityStore = remember { PersistedFitnessActivityStore(PlatformFitnessActivityPersistenceDriver) }
    var onboardingComplete by remember { mutableStateOf(OnboardingPreferenceStore.isCompleted()) }
    var healthSyncState by remember { mutableStateOf(SyncState.IDLE) }
    var healthSyncChannel by remember { mutableStateOf("health-history-import") }
    var lastReadSummary by remember { mutableStateOf("Nog geen health records gelezen") }
    var latestUiMessage by remember { mutableStateOf("Nog geen acties uitgevoerd") }
    var todaySteps by remember { mutableStateOf<Int?>(null) }
    var todayStepsHourlyTimeline by remember { mutableStateOf(emptyList<StepTimelinePoint>()) }
    var quickActivityEntries by remember { mutableStateOf(emptyList<QuickActivityEntry>()) }
    var fitnessSessions by remember { mutableStateOf(fitnessActivityStore.readSessions()) }
    var themeMode by rememberSaveable { mutableStateOf(HomeThemeMode.SYSTEM) }
    var fitnessBodyProfile by rememberSaveable {
        mutableStateOf(
            ProfilePreferenceStore.fitnessBodyProfileId()
                ?.let { stored -> FitnessBodyProfile.entries.firstOrNull { it.name == stored } }
                ?: FitnessBodyProfile.MALE
        )
    }
    var activeDetailDestination by rememberSaveable { mutableStateOf<HomeDetailDestination?>(null) }
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

                is FrontendEvent.TodayStepsUpdated -> {
                    todaySteps = event.totalSteps
                    todayStepsHourlyTimeline = event.buckets.map { bucket ->
                        StepTimelinePoint(
                            label = bucket.label,
                            steps = bucket.steps
                        )
                    }
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
    val dashboardSteps = todaySteps ?: derivedSteps
    val detailStepsTimeline = todayStepsHourlyTimeline.ifEmpty { fallbackStepTimeline(stepCount = dashboardSteps) }
    val dashboardTimeline = if (todayStepsHourlyTimeline.isEmpty()) {
        detailStepsTimeline
    } else {
        summarizeStepTimeline(detailStepsTimeline, pointsPerBucket = 3)
    }
    val derivedHeartRate = (68 - (metricEventCounts[HealthMetricType.HEART_RATE_BPM] ?: 0)).coerceIn(52, 90)
    val fitScore = (35 + (dashboardSteps / 180) - ((derivedHeartRate - 60).coerceAtLeast(0) / 2)).coerceIn(0, 100)
    val darkTheme = when (themeMode) {
        HomeThemeMode.SYSTEM -> isSystemInDarkTheme()
        HomeThemeMode.DARK -> true
        HomeThemeMode.LIGHT -> false
    }
    val activityEntries = fitnessSessions.map(::fitnessSessionToQuickActivityEntry) + quickActivityEntries

    PersonalHealthTheme(darkTheme = darkTheme) {
        if (onboardingComplete) {
            HomeScreen(
                fitScore = fitScore,
                steps = dashboardSteps,
                stepsTimeline = dashboardTimeline,
                detailStepsTimeline = detailStepsTimeline,
                fitnessSessions = fitnessSessions,
                fitnessBodyProfile = fitnessBodyProfile,
                heartRateBpm = derivedHeartRate,
                profileName = "Kees",
                themeMode = themeMode,
                activeDetailDestination = activeDetailDestination,
                onThemeModeSelected = { themeMode = it },
                onFitnessBodyProfileSelected = { profile ->
                    fitnessBodyProfile = profile
                    ProfilePreferenceStore.setFitnessBodyProfileId(profile.name)
                },
                onOpenStepsDetail = {
                    activeDetailDestination = HomeDetailDestination.STEPS
                    appScope.launch {
                        AppBus.events.publish(
                            FrontendEvent.NavigationChanged(
                                fromRoute = "home",
                                toRoute = "steps-detail",
                                emittedAtEpochMillis = currentEpochMillis()
                            )
                        )
                    }
                },
                onOpenFitnessDetail = {
                    activeDetailDestination = HomeDetailDestination.FITNESS
                    appScope.launch {
                        AppBus.events.publish(
                            FrontendEvent.NavigationChanged(
                                fromRoute = "home",
                                toRoute = "fitness-detail",
                                emittedAtEpochMillis = currentEpochMillis()
                            )
                        )
                    }
                },
                onOpenFitnessEditorDebug = {
                    activeDetailDestination = HomeDetailDestination.FITNESS_EDITOR_DEBUG
                    appScope.launch {
                        AppBus.events.publish(
                            FrontendEvent.NavigationChanged(
                                fromRoute = "fitness-detail",
                                toRoute = "fitness-editor-debug",
                                emittedAtEpochMillis = currentEpochMillis()
                            )
                        )
                    }
                },
                onCloseDetail = {
                    val fromRoute = when (activeDetailDestination) {
                        HomeDetailDestination.STEPS -> "steps-detail"
                        HomeDetailDestination.FITNESS -> "fitness-detail"
                        HomeDetailDestination.FITNESS_EDITOR_DEBUG -> "fitness-editor-debug"
                        null -> "home"
                    }
                    activeDetailDestination = null
                    appScope.launch {
                        AppBus.events.publish(
                            FrontendEvent.NavigationChanged(
                                fromRoute = fromRoute,
                                toRoute = "home",
                                emittedAtEpochMillis = currentEpochMillis()
                            )
                        )
                    }
                },
                onSaveFitnessSession = { session ->
                    fitnessActivityStore.upsertSession(session)
                    fitnessSessions = fitnessActivityStore.readSessions()
                    latestUiMessage = "${session.title} lokaal opgeslagen met ${session.exercises.size} oefeningen."
                },
                activityOptions = QuickActivityType.entries,
                activityEntries = activityEntries,
                onLogActivity = { activityType ->
                    quickActivityEntries = logQuickActivity(quickActivityEntries, activityType)
                    latestUiMessage = "${activityType.label} toegevoegd aan je activiteiten."
                },
                syncContent = {
                    HealthSyncStatusCard(
                        syncState = healthSyncState,
                        channel = healthSyncChannel,
                        lastReadSummary = lastReadSummary
                    )
                    HealthSyncStatsCard(
                        strategy = importStrategy,
                        intentReceivedCount = intentReceivedCount,
                        intentSkippedCount = intentSkippedCount,
                        intentAppliedCount = intentAppliedCount,
                        intentFailedCount = intentFailedCount,
                        metricEventCounts = metricEventCounts,
                        onAction = { actionId ->
                            appScope.launch {
                                executeHealthImportAction(
                                    actionId = actionId,
                                    publishHealthEvent = { event ->
                                        AppBus.events.publish(event)
                                    },
                                    publishUiMessage = { message ->
                                        AppBus.events.publish(
                                            FrontendEvent.UiFeedbackRequested(
                                                message = message,
                                                emittedAtEpochMillis = currentEpochMillis()
                                            )
                                        )
                                    }
                                )
                            }
                        },
                        latestUiMessage = latestUiMessage,
                        importRequestCount = importRequestCount,
                        importInProgress = healthSyncState == SyncState.SYNCING
                    )
                    PlatformHealthImportPanel(
                        onImportDocument = { document ->
                            applyImportedHealthDocument(document)
                        },
                        onImportMessage = { message ->
                            AppBus.events.publish(
                                FrontendEvent.UiFeedbackRequested(
                                    message = message,
                                    emittedAtEpochMillis = currentEpochMillis()
                                )
                            )
                        }
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

private suspend fun applyImportedHealthDocument(
    document: CanonicalHealthImportDocument
) {
    val snapshot = buildTodayStepsSnapshot(
        records = document.records,
        dayStartEpochMillis = document.window.startEpochMillis,
        dayEndEpochMillis = document.window.endEpochMillis,
        bucketSizeHours = 1
    )
    AppBus.events.publish(
        FrontendEvent.TodayStepsUpdated(
            totalSteps = snapshot.totalSteps,
            buckets = snapshot.buckets.map { bucket ->
                FrontendEvent.StepBucket(
                    label = bucket.label,
                    steps = bucket.steps
                )
            },
            emittedAtEpochMillis = document.exportedAtEpochMillis ?: currentEpochMillis()
        )
    )
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
    strategy: HealthImportStrategy,
    intentReceivedCount: Int,
    intentSkippedCount: Int,
    intentAppliedCount: Int,
    intentFailedCount: Int,
    metricEventCounts: Map<HealthMetricType, Int>,
    onAction: (HealthImportActionId) -> Unit,
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

private fun Map<HealthMetricType, Int>.bump(metric: HealthMetricType): Map<HealthMetricType, Int> {
    val updated = toMutableMap()
    updated[metric] = (updated[metric] ?: 0) + 1
    return updated
}
