package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.goals.CoachFocusGoal
import com.incedo.personalhealth.core.newssocial.NewsSocialFeed
import com.incedo.personalhealth.core.recommendations.DailyRecommendation

@Composable
fun HomeScreen(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    detailStepsTimeline: List<StepTimelinePoint>,
    detailHeartRateTimeline: List<HeartRateTimelinePoint>,
    detailWeightCatalog: HomeWeightChartCatalog,
    healthMetricCards: List<HomeHealthMetricCard>,
    activityMinutesToday: Int,
    fitnessSessions: List<FitnessActivitySession>,
    fitnessBodyProfile: FitnessBodyProfile,
    heartRateBpm: Int,
    dailyRecommendation: DailyRecommendation,
    newsSocialFeed: NewsSocialFeed,
    onboardingFocusGoal: CoachFocusGoal?,
    profileName: String,
    themeMode: HomeThemeMode,
    activeDetailDestination: HomeDetailDestination?,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    onFitnessBodyProfileSelected: (FitnessBodyProfile) -> Unit,
    onOpenStepsDetail: () -> Unit,
    onOpenHeartRateDetail: () -> Unit,
    onOpenWeightDetail: () -> Unit,
    onOpenHealthDataDetail: () -> Unit,
    onOpenFitnessDetail: () -> Unit,
    onOpenCoachDetail: (HomeDetailDestination) -> Unit,
    onOpenFitnessEditorDebug: () -> Unit,
    onCloseDetail: () -> Unit,
    onSaveFitnessSession: (FitnessActivitySession) -> Unit,
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityEntries: List<QuickActivityEntry>,
    nutritionEntries: List<NutritionLogEntry>,
    activityClockEpochMillis: Long,
    onStartActivity: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onRefreshHealthData: () -> Unit,
    onAddNutrition: () -> Unit,
    onUpdateNutrition: (NutritionLogEntry) -> Unit,
    syncContent: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
    profileContent: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.DASHBOARD) }
    var activeHealthMetricDetailId by rememberSaveable { mutableStateOf<String?>(null) }
    val palette = homePalette()
    val switchToTab: (HomeTab) -> Unit = { tab ->
        activeHealthMetricDetailId = null
        selectedTab = tab
        if (activeDetailDestination != null) {
            onCloseDetail()
        }
    }

    LaunchedEffect(activeDetailDestination) {
        if (activeDetailDestination != HomeDetailDestination.HEALTH_DATA) {
            activeHealthMetricDetailId = null
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(palette.backdropTop, palette.backdropBottom)))
    ) {
        val compact = maxWidth < 720.dp
        val expanded = maxWidth >= 1040.dp
        val outerPadding = if (compact) 16.dp else 24.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = outerPadding, vertical = 12.dp)
        ) {
            HomeNavigationHeader(
                selectedTab = selectedTab,
                compact = compact,
                expanded = expanded,
                onTabSelected = switchToTab
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeDetailDestination) {
                    HomeDetailDestination.STEPS -> StepDetailScreen(
                        steps = steps,
                        stepsTimeline = detailStepsTimeline,
                        onBack = onCloseDetail,
                        compact = compact
                    )

                    HomeDetailDestination.HEART_RATE -> HeartRateDetailScreen(
                        heartRateBpm = heartRateBpm,
                        timeline = detailHeartRateTimeline,
                        onBack = onCloseDetail,
                        compact = compact
                    )

                    HomeDetailDestination.WEIGHT -> WeightDetailScreen(
                        metric = resolveWeightMetric(healthMetricCards),
                        catalog = detailWeightCatalog,
                        onBack = onCloseDetail,
                        compact = compact
                    )

                    HomeDetailDestination.HEALTH_DATA -> if (activeHealthMetricDetailId == SLEEP_HEALTH_METRIC_ID) {
                        SleepDetailScreen(
                            metric = resolveSleepMetric(healthMetricCards),
                            onBack = { activeHealthMetricDetailId = null },
                            compact = compact
                        )
                    } else if (activeHealthMetricDetailId == BODY_WEIGHT_HEALTH_METRIC_ID) {
                        WeightDetailScreen(
                            metric = resolveWeightMetric(healthMetricCards),
                            catalog = detailWeightCatalog,
                            onBack = { activeHealthMetricDetailId = null },
                            compact = compact
                        )
                    } else {
                        HealthDataDetailScreen(
                            metrics = healthMetricCards,
                            onBack = onCloseDetail,
                            onRefresh = onRefreshHealthData,
                            onOpenSleepDetail = { activeHealthMetricDetailId = SLEEP_HEALTH_METRIC_ID },
                            onOpenWeightDetail = { activeHealthMetricDetailId = BODY_WEIGHT_HEALTH_METRIC_ID },
                            compact = compact
                        )
                    }

                    HomeDetailDestination.FITNESS -> FitnessActivityDetailScreen(
                        sessions = fitnessSessions,
                        bodyProfile = fitnessBodyProfile,
                        onBack = onCloseDetail,
                        onOpenDebugEditor = onOpenFitnessEditorDebug,
                        onSaveSession = onSaveFitnessSession,
                        compact = compact
                    )

                    HomeDetailDestination.FITNESS_EDITOR_DEBUG -> FitnessAnatomyEditorScreen(
                        bodyProfile = fitnessBodyProfile,
                        onBack = onCloseDetail,
                        compact = compact
                    )

                    HomeDetailDestination.DEV_TEST -> HomeDevTestScreen(
                        compact = compact,
                        onBack = onCloseDetail,
                        syncContent = syncContent,
                        profileContent = profileContent
                    )

                    HomeDetailDestination.COACH_INTAKE -> CoachSectionScreen(
                        page = CoachPage.INTAKE,
                        compact = compact,
                        fitScore = fitScore,
                        steps = steps,
                        heartRateBpm = heartRateBpm,
                        onboardingFocusGoal = onboardingFocusGoal,
                        onCloseCoachDetail = onCloseDetail,
                        onOpenCoachIntake = { onOpenCoachDetail(HomeDetailDestination.COACH_INTAKE) },
                        onOpenCoachGoals = { onOpenCoachDetail(HomeDetailDestination.COACH_GOALS) },
                        onOpenCoachDetails = { onOpenCoachDetail(HomeDetailDestination.COACH_DETAILS) },
                        onOpenCoachTrainingProgram = { onOpenCoachDetail(HomeDetailDestination.COACH_TRAINING_PROGRAM) },
                        onOpenDashboard = { switchToTab(HomeTab.DASHBOARD) },
                        onOpenLogbook = { switchToTab(HomeTab.LOG) },
                        onOpenProfile = { switchToTab(HomeTab.PROFILE) }
                    )

                    HomeDetailDestination.COACH_GOALS -> CoachSectionScreen(
                        page = CoachPage.GOALS,
                        compact = compact,
                        fitScore = fitScore,
                        steps = steps,
                        heartRateBpm = heartRateBpm,
                        onboardingFocusGoal = onboardingFocusGoal,
                        onCloseCoachDetail = onCloseDetail,
                        onOpenCoachIntake = { onOpenCoachDetail(HomeDetailDestination.COACH_INTAKE) },
                        onOpenCoachGoals = { onOpenCoachDetail(HomeDetailDestination.COACH_GOALS) },
                        onOpenCoachDetails = { onOpenCoachDetail(HomeDetailDestination.COACH_DETAILS) },
                        onOpenCoachTrainingProgram = { onOpenCoachDetail(HomeDetailDestination.COACH_TRAINING_PROGRAM) },
                        onOpenDashboard = { switchToTab(HomeTab.DASHBOARD) },
                        onOpenLogbook = { switchToTab(HomeTab.LOG) },
                        onOpenProfile = { switchToTab(HomeTab.PROFILE) }
                    )

                    HomeDetailDestination.COACH_DETAILS -> CoachSectionScreen(
                        page = CoachPage.DETAILS,
                        compact = compact,
                        fitScore = fitScore,
                        steps = steps,
                        heartRateBpm = heartRateBpm,
                        onboardingFocusGoal = onboardingFocusGoal,
                        onCloseCoachDetail = onCloseDetail,
                        onOpenCoachIntake = { onOpenCoachDetail(HomeDetailDestination.COACH_INTAKE) },
                        onOpenCoachGoals = { onOpenCoachDetail(HomeDetailDestination.COACH_GOALS) },
                        onOpenCoachDetails = { onOpenCoachDetail(HomeDetailDestination.COACH_DETAILS) },
                        onOpenCoachTrainingProgram = { onOpenCoachDetail(HomeDetailDestination.COACH_TRAINING_PROGRAM) },
                        onOpenDashboard = { switchToTab(HomeTab.DASHBOARD) },
                        onOpenLogbook = { switchToTab(HomeTab.LOG) },
                        onOpenProfile = { switchToTab(HomeTab.PROFILE) }
                    )

                    HomeDetailDestination.COACH_TRAINING_PROGRAM -> CoachSectionScreen(
                        page = CoachPage.TRAINING_PROGRAM,
                        compact = compact,
                        fitScore = fitScore,
                        steps = steps,
                        heartRateBpm = heartRateBpm,
                        onboardingFocusGoal = onboardingFocusGoal,
                        onCloseCoachDetail = onCloseDetail,
                        onOpenCoachIntake = { onOpenCoachDetail(HomeDetailDestination.COACH_INTAKE) },
                        onOpenCoachGoals = { onOpenCoachDetail(HomeDetailDestination.COACH_GOALS) },
                        onOpenCoachDetails = { onOpenCoachDetail(HomeDetailDestination.COACH_DETAILS) },
                        onOpenCoachTrainingProgram = { onOpenCoachDetail(HomeDetailDestination.COACH_TRAINING_PROGRAM) },
                        onOpenDashboard = { switchToTab(HomeTab.DASHBOARD) },
                        onOpenLogbook = { switchToTab(HomeTab.LOG) },
                        onOpenProfile = { switchToTab(HomeTab.PROFILE) }
                    )

                    null -> {
                        HomeTabContent(
                            selectedTab = selectedTab,
                            fitScore = fitScore,
                            steps = steps,
                            stepsTimeline = stepsTimeline,
                            healthMetricCards = healthMetricCards,
                            activityMinutesToday = activityMinutesToday,
                            heartRateBpm = heartRateBpm,
                            dailyRecommendation = dailyRecommendation,
                            newsSocialFeed = newsSocialFeed,
                            onboardingFocusGoal = onboardingFocusGoal,
                            profileName = profileName,
                            themeMode = themeMode,
                            onThemeModeSelected = onThemeModeSelected,
                            fitnessBodyProfile = fitnessBodyProfile,
                            onFitnessBodyProfileSelected = onFitnessBodyProfileSelected,
                            activityOptions = activityOptions,
                            activeActivity = activeActivity,
                            activityEntries = activityEntries,
                            nutritionEntries = nutritionEntries,
                            activityClockEpochMillis = activityClockEpochMillis,
                            onStartActivity = onStartActivity,
                            onStopActivity = onStopActivity,
                            onOpenHealthDataDetail = onOpenHealthDataDetail,
                            onAddNutrition = onAddNutrition,
                            onUpdateNutrition = onUpdateNutrition,
                            onOpenFitnessDetail = onOpenFitnessDetail,
                            onOpenStepsDetail = onOpenStepsDetail,
                            onOpenHeartRateDetail = onOpenHeartRateDetail,
                            onOpenWeightDetail = onOpenWeightDetail,
                            onOpenDevTest = { onOpenCoachDetail(HomeDetailDestination.DEV_TEST) },
                            onNavigateToTab = switchToTab,
                            onOpenCoachDetail = onOpenCoachDetail,
                            syncContent = syncContent,
                            profileContent = profileContent,
                            compact = compact
                        )
                    }
                }
            }
            HomeNavigationFooter(
                selectedTab = selectedTab,
                compact = compact,
                expanded = expanded,
                onTabSelected = switchToTab
            )
        }
    }
}
