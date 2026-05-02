package com.incedo.personalhealth.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import com.incedo.personalhealth.core.health.currentEpochMillis
import com.incedo.personalhealth.core.newssocial.NewsSocialFeedRequest
import com.incedo.personalhealth.core.newssocial.StubNewsSocialFeedApi
import com.incedo.personalhealth.core.newssocial.defaultNewsSocialFeed
import com.incedo.personalhealth.core.onboarding.OnboardingUiState
import com.incedo.personalhealth.core.recommendations.DailyRecommendationRequest
import com.incedo.personalhealth.core.recommendations.StubRecommendationOfDayApi
import com.incedo.personalhealth.core.recommendations.defaultDailyRecommendation
import com.incedo.personalhealth.core.wellbeing.WellbeingEvent
import com.incedo.personalhealth.core.wellbeing.defaultSelectedSocialAppPackages
import com.incedo.personalhealth.core.wellbeing.emptyScreenTimeSummary
import com.incedo.personalhealth.core.wellbeing.resolveSelectedSocialApps
import com.incedo.personalhealth.feature.home.HeartRateTimelinePoint
import com.incedo.personalhealth.feature.home.HomeDetailDestination
import com.incedo.personalhealth.feature.home.HomeHealthMetricCard
import com.incedo.personalhealth.feature.home.HomeScreen
import com.incedo.personalhealth.feature.home.HomeWeightChartCatalog
import com.incedo.personalhealth.feature.home.PersistedActivityTrackingStore
import com.incedo.personalhealth.feature.home.PlatformActivityTrackingPersistenceDriver
import com.incedo.personalhealth.feature.home.FitnessBodyProfile
import com.incedo.personalhealth.feature.home.PersistedNutritionLogStore
import com.incedo.personalhealth.feature.home.PersistedFitnessActivityStore
import com.incedo.personalhealth.feature.home.PlatformNutritionLogPersistenceDriver
import com.incedo.personalhealth.feature.home.PlatformFitnessActivityPersistenceDriver
import com.incedo.personalhealth.feature.home.NutritionLogEntry
import com.incedo.personalhealth.feature.home.QuickActivityType
import com.incedo.personalhealth.feature.home.StepTimelinePoint
import com.incedo.personalhealth.feature.home.HomeThemeMode
import com.incedo.personalhealth.feature.home.createNutritionLogEntry
import com.incedo.personalhealth.feature.home.currentNutritionEpochMillis
import com.incedo.personalhealth.feature.home.elapsedDurationMillis
import com.incedo.personalhealth.feature.home.fallbackHeartRateTimeline
import com.incedo.personalhealth.feature.home.fallbackStepTimeline
import com.incedo.personalhealth.feature.home.formatActivitySummaryDuration
import com.incedo.personalhealth.feature.home.fitnessSessionToQuickActivityEntry
import com.incedo.personalhealth.feature.home.localDayWindow
import com.incedo.personalhealth.feature.home.rememberActivityClock
import com.incedo.personalhealth.feature.home.summarizeStepTimeline
import com.incedo.personalhealth.feature.home.toQuickActivityEntry
import com.incedo.personalhealth.feature.home.totalFitnessActivityMinutes
import com.incedo.personalhealth.feature.home.totalTrackedActivityMinutes
import com.incedo.personalhealth.feature.onboarding.OnboardingRoute
import kotlinx.coroutines.launch

@Composable
fun PersonalHealthApp() {
    val appScope = rememberCoroutineScope()
    val importStrategy = remember { currentHealthImportStrategy() }
    val fitnessActivityStore = remember { PersistedFitnessActivityStore(PlatformFitnessActivityPersistenceDriver) }
    val activityTrackingStore = remember { PersistedActivityTrackingStore(PlatformActivityTrackingPersistenceDriver) }
    val nutritionLogStore = remember { PersistedNutritionLogStore(PlatformNutritionLogPersistenceDriver) }
    val newsSocialFeedApi = remember { StubNewsSocialFeedApi() }
    val recommendationApi = remember { StubRecommendationOfDayApi() }
    val initialDashboardHealthState = remember { readPersistedDashboardHealthUiState() }
    remember { runHomeStorageMaintenance(fitnessActivityStore = fitnessActivityStore, activityTrackingStore = activityTrackingStore) }
    var onboardingComplete by remember { mutableStateOf(OnboardingPreferenceStore.isCompleted()) }
    val initialOnboardingState = remember {
        OnboardingUiState(
            stepIndex = OnboardingPreferenceStore.stepIndex(),
            selectedGoal = onboardingGoalFromId(OnboardingPreferenceStore.selectedGoalId()),
            completed = OnboardingPreferenceStore.isCompleted()
        )
    }
    var onboardingGoal by remember { mutableStateOf(initialOnboardingState.selectedGoal) }; var healthSyncState by remember { mutableStateOf(SyncState.IDLE) }
    var healthSyncChannel by remember { mutableStateOf("health-history-import") }
    var lastReadSummary by remember { mutableStateOf("Nog geen health records gelezen") }
    var latestUiMessage by remember { mutableStateOf("Nog geen acties uitgevoerd") }
    var todaySteps by remember { mutableStateOf<Int?>(null) }
    var todayStepsHourlyTimeline by remember { mutableStateOf(emptyList<StepTimelinePoint>()) }
    var todayHeartRateBpm by remember { mutableStateOf<Int?>(null) }
    var todayHeartRateTimeline by remember { mutableStateOf(emptyList<HeartRateTimelinePoint>()) }
    var bodyWeightCatalog by remember { mutableStateOf(initialDashboardHealthState.bodyWeightCatalog) }
    var dashboardHealthMetricCards by remember { mutableStateOf(initialDashboardHealthState.healthMetricCards) }
    var lastDashboardEventCache by remember { mutableStateOf<DashboardHealthEventCache?>(null) }
    var nutritionEntries by remember { mutableStateOf(nutritionLogStore.readEntries()) }
    var fitnessSessions by remember { mutableStateOf(fitnessActivityStore.readSessions()) }
    var trackedActivitySnapshot by remember { mutableStateOf(activityTrackingStore.readSnapshot()) }
    var selectedSocialPackages by remember { mutableStateOf(ProfilePreferenceStore.selectedSocialAppPackageIds() ?: defaultSelectedSocialAppPackages()) }
    var screenTimeSummary by remember { mutableStateOf(emptyScreenTimeSummary(resolveSelectedSocialApps(selectedSocialPackages))) }
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

    PlatformDemoSeedEffect(fitnessActivityStore, activityTrackingStore, nutritionLogStore) { seeded, refreshedNutritionEntries, refreshedFitnessSessions, refreshedActivitySnapshot ->
        onboardingComplete = OnboardingPreferenceStore.isCompleted()
        nutritionEntries = refreshedNutritionEntries
        fitnessSessions = refreshedFitnessSessions
        trackedActivitySnapshot = refreshedActivitySnapshot
        if (seeded) latestUiMessage = "Platform demo data geladen."
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

                is FrontendEvent.UiFeedbackRequested -> latestUiMessage = event.message

                is FrontendEvent.TodayStepsUpdated -> {
                    todaySteps = event.totalSteps
                    todayStepsHourlyTimeline = event.toStepTimelinePoints()
                }

                is FrontendEvent.TodayHeartRateUpdated -> {
                    todayHeartRateBpm = event.latestHeartRateBpm ?: event.averageHeartRateBpm
                    todayHeartRateTimeline = event.toHeartRateTimelinePoints()
                }

                is HealthEvent.DashboardRecordsUpdated -> {
                    lastDashboardEventCache = event.toCache()
                    val dashboardHealthUiState = persistDashboardHealthUiState(event)
                    dashboardHealthMetricCards = dashboardHealthUiState.healthMetricCards
                    bodyWeightCatalog = dashboardHealthUiState.bodyWeightCatalog
                }

                is FrontendEvent.TodayHealthSummariesUpdated -> Unit
                is WellbeingEvent.ScreenTimeSummaryUpdated -> screenTimeSummary = event.summary

                is HealthEvent.RecordsRead -> {
                    val sourceLabel = event.source.name.lowercase()
                    lastReadSummary = "$sourceLabel: ${event.count} records"
                }

                is HealthEvent.SyncRequested -> {
                    importRequestCount += 1
                    latestUiMessage = "Importverzoek verstuurd (${event.metrics.size} metrics)"
                }

                is HealthEvent.LiveSyncIntentReceived -> intentReceivedCount += 1

                is HealthEvent.LiveSyncIntentSkippedDuplicate -> intentSkippedCount += 1

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

                is HealthEvent.LiveSyncStepsIntentApplied -> metricEventCounts = metricEventCounts.bump(HealthMetricType.STEPS)
                is HealthEvent.LiveSyncHeartRateIntentApplied -> metricEventCounts = metricEventCounts.bump(HealthMetricType.HEART_RATE_BPM)

                is HealthEvent.LiveSyncSleepIntentApplied -> {
                    metricEventCounts = metricEventCounts.bump(HealthMetricType.SLEEP_DURATION_MINUTES)
                }

                is HealthEvent.LiveSyncActiveEnergyIntentApplied -> {
                    metricEventCounts = metricEventCounts.bump(HealthMetricType.ACTIVE_ENERGY_KCAL)
                }

                is HealthEvent.LiveSyncBodyWeightIntentApplied -> metricEventCounts = metricEventCounts.bump(HealthMetricType.BODY_WEIGHT_KG)
            }
        }
    }
    val healthMetricCards = buildScreenTimeMetricCards(screenTimeSummary) + dashboardHealthMetricCards

    val derivedSteps = 4500 + (metricEventCounts[HealthMetricType.STEPS] ?: 0) * 250
    val dashboardSteps = todaySteps ?: derivedSteps
    val detailStepsTimeline = todayStepsHourlyTimeline.ifEmpty { fallbackStepTimeline(stepCount = dashboardSteps) }
    val dashboardTimeline = if (todayStepsHourlyTimeline.isEmpty()) {
        detailStepsTimeline
    } else {
        summarizeStepTimeline(detailStepsTimeline, pointsPerBucket = 3)
    }
    val activityClockEpochMillis = rememberActivityClock(trackedActivitySnapshot.activeSession != null)
    val activityDayWindow = localDayWindow(activityClockEpochMillis)
    val activityMinutesToday = totalTrackedActivityMinutes(
        completedSessions = trackedActivitySnapshot.completedSessions,
        activeSession = trackedActivitySnapshot.activeSession,
        nowEpochMillis = activityClockEpochMillis,
        dayWindow = activityDayWindow
    ) + totalFitnessActivityMinutes(
        sessions = fitnessSessions,
        dayWindow = activityDayWindow
    )
    val derivedHeartRate = (68 - (metricEventCounts[HealthMetricType.HEART_RATE_BPM] ?: 0)).coerceIn(52, 90); val dashboardHeartRate = todayHeartRateBpm ?: derivedHeartRate
    val fitScore = (35 + (dashboardSteps / 180) - ((dashboardHeartRate - 60).coerceAtLeast(0) / 2)).coerceIn(0, 100)
    val recommendationRequest = DailyRecommendationRequest(
        fitScore = fitScore,
        heartRateBpm = dashboardHeartRate,
        steps = dashboardSteps,
        activityMinutesToday = activityMinutesToday,
        profileName = "Kees"
    )
    val dailyRecommendation by produceState(
        initialValue = defaultDailyRecommendation(recommendationRequest),
        recommendationRequest.fitScore,
        recommendationRequest.heartRateBpm,
        recommendationRequest.steps,
        recommendationRequest.activityMinutesToday,
        recommendationRequest.profileName
    ) {
        value = recommendationApi.getRecommendationOfDay(recommendationRequest)
    }
    val newsSocialRequest = NewsSocialFeedRequest(profileName = "Kees"); val newsSocialFeed by produceState(
        initialValue = defaultNewsSocialFeed(newsSocialRequest), newsSocialRequest.profileName
    ) {
        value = newsSocialFeedApi.getFeed(newsSocialRequest)
    }
    val detailHeartRateTimeline = todayHeartRateTimeline.ifEmpty { fallbackHeartRateTimeline(
        averageBpm = dashboardHeartRate,
        sampleCount = metricEventCounts[HealthMetricType.HEART_RATE_BPM] ?: 0
    ) }
    val darkTheme = when (themeMode) { HomeThemeMode.SYSTEM -> isSystemInDarkTheme(); HomeThemeMode.DARK -> true; HomeThemeMode.LIGHT -> false }
    val activityEntries = (
        fitnessSessions.map(::fitnessSessionToQuickActivityEntry) +
            trackedActivitySnapshot.completedSessions.map { it.toQuickActivityEntry() }
        ).sortedByDescending { it.createdAtEpochMillis }

    PersonalHealthTheme(darkTheme = darkTheme) {
        if (onboardingComplete && !isOnboardingPreviewRequested()) {
            fun openDetail(destination: HomeDetailDestination, fromRoute: String = "home") {
                activeDetailDestination = destination
                publishNavigationChange(appScope, fromRoute = fromRoute, toRoute = destination.routeName())
            }
            HomeScreen(
                fitScore = fitScore,
                steps = dashboardSteps,
                stepsTimeline = dashboardTimeline,
                detailStepsTimeline = detailStepsTimeline,
                detailHeartRateTimeline = detailHeartRateTimeline,
                detailWeightCatalog = bodyWeightCatalog,
                healthMetricCards = healthMetricCards,
                activityMinutesToday = activityMinutesToday,
                fitnessSessions = fitnessSessions,
                fitnessBodyProfile = fitnessBodyProfile,
                heartRateBpm = dashboardHeartRate,
                dailyRecommendation = dailyRecommendation,
                newsSocialFeed = newsSocialFeed,
                onboardingFocusGoal = onboardingGoal?.toCoachFocusGoal(),
                profileName = "Kees",
                themeMode = themeMode,
                activeDetailDestination = activeDetailDestination,
                onThemeModeSelected = { themeMode = it },
                onFitnessBodyProfileSelected = { profile ->
                    fitnessBodyProfile = profile
                    ProfilePreferenceStore.setFitnessBodyProfileId(profile.name)
                },
                onOpenStepsDetail = { openDetail(HomeDetailDestination.STEPS) },
                onOpenHeartRateDetail = { openDetail(HomeDetailDestination.HEART_RATE) },
                onOpenWeightDetail = { openDetail(HomeDetailDestination.WEIGHT) },
                onOpenHealthDataDetail = { openDetail(HomeDetailDestination.HEALTH_DATA) },
                onOpenFitnessDetail = { openDetail(HomeDetailDestination.FITNESS) },
                onOpenCoachDetail = ::openDetail,
                onOpenFitnessEditorDebug = { openDetail(HomeDetailDestination.FITNESS_EDITOR_DEBUG, HomeDetailDestination.FITNESS.routeName()) },
                onCloseDetail = {
                    val fromRoute = activeDetailDestination?.routeName() ?: "home"
                    activeDetailDestination = null
                    publishNavigationChange(appScope, fromRoute = fromRoute, toRoute = "home")
                },
                onSaveFitnessSession = { session ->
                    fitnessActivityStore.upsertSession(session)
                    fitnessSessions = fitnessActivityStore.readSessions()
                    latestUiMessage = "${session.title} lokaal opgeslagen met ${session.exercises.size} oefeningen."
                },
                activityOptions = QuickActivityType.entries,
                activeActivity = trackedActivitySnapshot.activeSession,
                activityEntries = activityEntries,
                nutritionEntries = nutritionEntries,
                activityClockEpochMillis = activityClockEpochMillis,
                onStartActivity = { activityType ->
                    if (trackedActivitySnapshot.activeSession != null) {
                        latestUiMessage = "${trackedActivitySnapshot.activeSession!!.type.label} loopt al. Stop eerst de actieve sessie."
                    } else {
                        trackedActivitySnapshot = activityTrackingStore.startActivity(
                            type = activityType,
                            nowEpochMillis = activityClockEpochMillis
                        )
                        latestUiMessage = "${activityType.label} gestart."
                    }
                },
                onStopActivity = {
                    val stoppedSession = trackedActivitySnapshot.activeSession
                    trackedActivitySnapshot = activityTrackingStore.stopActiveActivity(activityClockEpochMillis)
                    latestUiMessage = if (stoppedSession == null) {
                        "Er loopt geen activiteit om te stoppen."
                    } else {
                        "${stoppedSession.type.label} gestopt na ${
                            formatActivitySummaryDuration(
                                stoppedSession.elapsedDurationMillis(activityClockEpochMillis)
                            )
                        }."
                    }
                },
                onRefreshHealthData = {
                    appScope.launch {
                        AppBus.events.publish(HealthEvent.SyncRequested(DEFAULT_IMPORT_METRICS, currentEpochMillis()))
                        AppBus.events.publish(WellbeingEvent.ScreenTimeRefreshRequested(currentEpochMillis()))
                    }
                },
                onAddNutrition = {
                    val entry = createNutritionLogEntry(
                        existingEntries = nutritionEntries,
                        nowEpochMillis = currentNutritionEpochMillis()
                    )
                    nutritionLogStore.addEntry(entry)
                    nutritionEntries = nutritionLogStore.readEntries()
                    latestUiMessage = "Nutrition lokaal opgeslagen voor ${entry.details.posterName}."
                },
                onUpdateNutrition = { entry ->
                    nutritionLogStore.addEntry(entry)
                    nutritionEntries = nutritionLogStore.readEntries()
                    latestUiMessage = "Nutrition entry bijgewerkt."
                },
                syncContent = {
                    HealthSyncToolsContent(
                        healthSyncState = healthSyncState,
                        healthSyncChannel = healthSyncChannel,
                        lastReadSummary = lastReadSummary,
                        importStrategy = importStrategy,
                        intentReceivedCount = intentReceivedCount,
                        intentSkippedCount = intentSkippedCount,
                        intentAppliedCount = intentAppliedCount,
                        intentFailedCount = intentFailedCount,
                        metricEventCounts = metricEventCounts,
                        latestUiMessage = latestUiMessage,
                        importRequestCount = importRequestCount,
                        onImportAction = { actionId ->
                            appScope.launch {
                                executeHealthImportAction(
                                    actionId = actionId,
                                    publishHealthEvent = { event -> AppBus.events.publish(event) },
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
                        onRecalculateDebug = lastDashboardEventCache?.let {
                            {
                                appScope.launch {
                                    it.recalculate(AppBus.events)
                                    latestUiMessage = "Dashboard health snapshots opnieuw berekend."
                                }
                            }
                        },
                        onImportDocument = { document -> applyImportedHealthDocument(document) },
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
                    ProfileWellbeingContent(
                        appScope = appScope,
                        screenTimeSummary = screenTimeSummary,
                        selectedSocialPackages = selectedSocialPackages,
                        latestUiMessage = latestUiMessage,
                        onSelectedPackagesChanged = { selectedSocialPackages = it }
                    )
                }
            )
        } else {
            OnboardingRoute(
                initialState = initialOnboardingState,
                onStateChanged = { state ->
                    onboardingGoal = state.selectedGoal
                    OnboardingPreferenceStore.setStepIndex(state.stepIndex)
                    OnboardingPreferenceStore.setSelectedGoalId(state.selectedGoal?.name)
                    OnboardingPreferenceStore.setCompleted(state.completed)
                },
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
    publishDashboardHealthEvents(
        records = document.records,
        dayStartEpochMillis = document.window.startEpochMillis,
        dayEndEpochMillis = document.window.endEpochMillis,
        emittedAtEpochMillis = document.exportedAtEpochMillis ?: currentEpochMillis(),
        eventBus = AppBus.events
    )
}

private fun Map<HealthMetricType, Int>.bump(metric: HealthMetricType): Map<HealthMetricType, Int> {
    val updated = toMutableMap()
    updated[metric] = (updated[metric] ?: 0) + 1
    return updated
}
