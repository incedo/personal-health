package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.incedo.personalhealth.core.goals.CoachFocusGoal
import com.incedo.personalhealth.core.newssocial.NewsSocialFeed
import com.incedo.personalhealth.core.recommendations.DailyRecommendation

@Composable
internal fun HomeTabContent(
    selectedTab: HomeTab,
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    healthMetricCards: List<HomeHealthMetricCard>,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    dailyRecommendation: DailyRecommendation,
    newsSocialFeed: NewsSocialFeed,
    onboardingFocusGoal: CoachFocusGoal?,
    profileName: String,
    themeMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    fitnessBodyProfile: FitnessBodyProfile,
    onFitnessBodyProfileSelected: (FitnessBodyProfile) -> Unit,
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityEntries: List<QuickActivityEntry>,
    nutritionEntries: List<NutritionLogEntry>,
    activityClockEpochMillis: Long,
    onStartActivity: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onAddNutrition: () -> Unit,
    onUpdateNutrition: (NutritionLogEntry) -> Unit,
    onOpenFitnessDetail: () -> Unit,
    onOpenStepsDetail: () -> Unit,
    onOpenHeartRateDetail: () -> Unit,
    onOpenWeightDetail: () -> Unit,
    onOpenHealthDataDetail: () -> Unit,
    onOpenCoachDetail: (HomeDetailDestination) -> Unit,
    onOpenDevTest: () -> Unit,
    onNavigateToTab: (HomeTab) -> Unit,
    syncContent: @Composable ColumnScope.() -> Unit,
    profileContent: @Composable ColumnScope.() -> Unit,
    compact: Boolean
) {
    when (selectedTab) {
        HomeTab.DASHBOARD -> DashboardContent(
            fitScore = fitScore,
            steps = steps,
            stepsTimeline = stepsTimeline,
            weightSummary = resolveHealthMetricValue(
                metrics = healthMetricCards,
                metricId = BODY_WEIGHT_HEALTH_METRIC_ID
            ),
            activityMinutesToday = activityMinutesToday,
            heartRateBpm = heartRateBpm,
            dailyRecommendation = dailyRecommendation,
            profileName = profileName,
            activityOptions = activityOptions,
            activeActivity = activeActivity,
            activityClockEpochMillis = activityClockEpochMillis,
            onStartActivity = onStartActivity,
            onStopActivity = onStopActivity,
            onLogNutrition = onAddNutrition,
            onOpenStepsDetail = onOpenStepsDetail,
            onOpenHeartRateDetail = onOpenHeartRateDetail,
            onOpenWeightDetail = onOpenWeightDetail,
            onOpenHealthDataDetail = onOpenHealthDataDetail,
            compact = compact
        )

        HomeTab.NEWS -> HomeSectionScreen(
            tab = HomeTab.NEWS,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Nieuws & social",
                    title = newsSocialFeed.heroTitle,
                    subtitle = newsSocialFeed.heroSubtitle,
                    accent = homePalette().warm,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = newsSocialFeed.statusLabel,
                            value = newsSocialFeed.statusValue
                        )
                    }
                )
            },
            bodyContent = {
                NewsSocialSection(feed = newsSocialFeed)
            }
        )

        HomeTab.COACH -> CoachSectionScreen(
            page = CoachPage.OVERVIEW,
            compact = compact,
            fitScore = fitScore,
            steps = steps,
            heartRateBpm = heartRateBpm,
            onboardingFocusGoal = onboardingFocusGoal,
            onCloseCoachDetail = {},
            onOpenCoachIntake = { onOpenCoachDetail(HomeDetailDestination.COACH_INTAKE) },
            onOpenCoachGoals = { onOpenCoachDetail(HomeDetailDestination.COACH_GOALS) },
            onOpenCoachDetails = { onOpenCoachDetail(HomeDetailDestination.COACH_DETAILS) },
            onOpenCoachTrainingProgram = { onOpenCoachDetail(HomeDetailDestination.COACH_TRAINING_PROGRAM) },
            onOpenDashboard = { onNavigateToTab(HomeTab.DASHBOARD) },
            onOpenLogbook = { onNavigateToTab(HomeTab.LOG) },
            onOpenProfile = { onNavigateToTab(HomeTab.PROFILE) }
        )

        HomeTab.LOG -> HomeSectionScreen(
            tab = HomeTab.LOG,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Logboek",
                    title = "Eten, drinken en activiteit",
                    subtitle = "Alles wat je toevoegt komt hier samen, met een lopende timer zolang je met een activiteit bezig bent.",
                    accent = homePalette().accent,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Vandaag",
                            value = activityEntries.size.toString()
                        )
                    }
                )
            },
            bodyContent = {
                LogbookSection(
                    activityOptions = activityOptions,
                    activeActivity = activeActivity,
                    activityEntries = activityEntries,
                    nutritionEntries = nutritionEntries,
                    nowEpochMillis = activityClockEpochMillis,
                    onStartActivity = onStartActivity,
                    onStopActivity = onStopActivity,
                    onAddNutrition = onAddNutrition,
                    onUpdateNutrition = onUpdateNutrition
                )
            }
        )

        HomeTab.PROFILE -> HomeSectionScreen(
            tab = HomeTab.PROFILE,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Profiel",
                    title = profileName,
                    subtitle = "Identity, vitals, devices, doelen, privacy en voorkeuren op een vaste plek.",
                    accent = homePalette().accent,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Streak",
                            value = "14 dagen"
                        )
                    }
                )
            },
            bodyContent = {
                ProfileDesignContent(
                    profileName = profileName,
                    fitScore = fitScore,
                    themeMode = themeMode,
                    onThemeModeSelected = onThemeModeSelected,
                    fitnessBodyProfile = fitnessBodyProfile,
                    onFitnessBodyProfileSelected = onFitnessBodyProfileSelected,
                    onOpenDevTest = onOpenDevTest
                )
            }
        )
    }
}
