package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeTabContent(
    selectedTab: HomeTab,
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    healthMetricCards: List<HomeHealthMetricCard>,
    activityMinutesToday: Int,
    heartRateBpm: Int,
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
                    title = "Wat speelt er vandaag",
                    subtitle = "Een lichte feed met inspiratie, communitymomenten en health-updates in plaats van test-imports.",
                    accent = homePalette().warm,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Live",
                            value = "3 updates"
                        )
                    }
                )
            },
            bodyContent = {
                NewsSocialSection()
            }
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
                    title = "Jouw basis en voorkeuren",
                    subtitle = "Houd accountinstellingen, profielkeuzes en import/testfuncties op een vaste plek bij elkaar.",
                    accent = homePalette().accent,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Import",
                            value = "Beschikbaar"
                        )
                    }
                )
            },
            bodyContent = {
                ThemeModeCard(
                    selectedMode = themeMode,
                    onThemeModeSelected = onThemeModeSelected
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(18.dp))
                FitnessBodyProfileCard(
                    selectedProfile = fitnessBodyProfile,
                    onProfileSelected = onFitnessBodyProfileSelected
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(18.dp))
                HomePanel(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                    androidx.compose.material3.Text(
                        text = "Import en test",
                        style = MaterialTheme.typography.titleLarge,
                        color = homePalette().textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = androidx.compose.ui.Modifier.height(6.dp))
                    androidx.compose.material3.Text(
                        text = "Hier staan de tijdelijke import- en synctools, zodat ze niet meer in de hoofdnav zitten.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = homePalette().textSecondary
                    )
                    Spacer(modifier = androidx.compose.ui.Modifier.height(18.dp))
                    syncContent()
                }
                Spacer(modifier = androidx.compose.ui.Modifier.height(18.dp))
                profileContent()
            }
        )
    }
}
