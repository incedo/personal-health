package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    detailStepsTimeline: List<StepTimelinePoint>,
    fitnessSessions: List<FitnessActivitySession>,
    fitnessBodyProfile: FitnessBodyProfile,
    heartRateBpm: Int,
    profileName: String,
    themeMode: HomeThemeMode,
    activeDetailDestination: HomeDetailDestination?,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    onFitnessBodyProfileSelected: (FitnessBodyProfile) -> Unit,
    onOpenStepsDetail: () -> Unit,
    onOpenFitnessDetail: () -> Unit,
    onOpenFitnessEditorDebug: () -> Unit,
    onCloseDetail: () -> Unit,
    onSaveFitnessSession: (FitnessActivitySession) -> Unit,
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    nutritionEntries: List<NutritionLogEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    onAddNutrition: () -> Unit,
    onUpdateNutrition: (NutritionLogEntry) -> Unit,
    syncContent: @Composable ColumnScope.() -> Unit,
    profileContent: @Composable ColumnScope.() -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.DASHBOARD) }
    val palette = homePalette()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(palette.backdropTop, palette.backdropBottom)))
    ) {
        val compact = maxWidth < 720.dp
        val outerPadding = if (compact) 16.dp else 24.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = outerPadding, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(activeDetailDestination, selectedTab) {
                        var dragOffset = 0f
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount
                            },
                            onDragEnd = {
                                when {
                                    activeDetailDestination != null && dragOffset > 80f -> onCloseDetail()
                                    activeDetailDestination == null && dragOffset > 80f -> {
                                        selectedTab = selectedTab.previous()
                                    }
                                    activeDetailDestination == null && dragOffset < -80f -> {
                                        selectedTab = selectedTab.next()
                                    }
                                }
                                dragOffset = 0f
                            }
                        )
                    }
            ) {
                when (activeDetailDestination) {
                    HomeDetailDestination.STEPS -> StepDetailScreen(
                        steps = steps,
                        stepsTimeline = detailStepsTimeline,
                        onBack = onCloseDetail,
                        compact = compact
                    )

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

                    null -> {
                        HomeTabContent(
                            selectedTab = selectedTab,
                            fitScore = fitScore,
                            steps = steps,
                            stepsTimeline = stepsTimeline,
                            heartRateBpm = heartRateBpm,
                            profileName = profileName,
                            themeMode = themeMode,
                            onThemeModeSelected = onThemeModeSelected,
                            fitnessBodyProfile = fitnessBodyProfile,
                            onFitnessBodyProfileSelected = onFitnessBodyProfileSelected,
                            activityOptions = activityOptions,
                            activityEntries = activityEntries,
                            nutritionEntries = nutritionEntries,
                            onLogActivity = onLogActivity,
                            onAddNutrition = onAddNutrition,
                            onUpdateNutrition = onUpdateNutrition,
                            onOpenFitnessDetail = onOpenFitnessDetail,
                            onOpenStepsDetail = onOpenStepsDetail,
                            syncContent = syncContent,
                            profileContent = profileContent,
                            compact = compact
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HomeBottomTabs(
                selectedTab = selectedTab,
                compact = compact,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

private fun HomeTab.previous(): HomeTab {
    val index = HomeTab.entries.indexOf(this)
    return HomeTab.entries[(index - 1).coerceAtLeast(0)]
}

private fun HomeTab.next(): HomeTab {
    val index = HomeTab.entries.indexOf(this)
    return HomeTab.entries[(index + 1).coerceAtMost(HomeTab.entries.lastIndex)]
}

@Composable
private fun HomeTabContent(
    selectedTab: HomeTab,
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    profileName: String,
    themeMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    fitnessBodyProfile: FitnessBodyProfile,
    onFitnessBodyProfileSelected: (FitnessBodyProfile) -> Unit,
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    nutritionEntries: List<NutritionLogEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    onAddNutrition: () -> Unit,
    onUpdateNutrition: (NutritionLogEntry) -> Unit,
    onOpenFitnessDetail: () -> Unit,
    onOpenStepsDetail: () -> Unit,
    syncContent: @Composable ColumnScope.() -> Unit,
    profileContent: @Composable ColumnScope.() -> Unit,
    compact: Boolean
) {
    when (selectedTab) {
        HomeTab.DASHBOARD -> DashboardContent(
            fitScore = fitScore,
            steps = steps,
            stepsTimeline = stepsTimeline,
            heartRateBpm = heartRateBpm,
            profileName = profileName,
            activityOptions = activityOptions,
            activityEntries = activityEntries,
            onLogActivity = onLogActivity,
            onOpenFitnessDetail = onOpenFitnessDetail,
            onOpenStepsDetail = onOpenStepsDetail,
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
                    subtitle = "Alles wat je toevoegt komt hier samen, zodat dagelijkse logging niet meer verstopt zit op home.",
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
                    activityEntries = activityEntries,
                    nutritionEntries = nutritionEntries,
                    onLogActivity = onLogActivity,
                    onOpenFitnessDetail = onOpenFitnessDetail,
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
                Spacer(modifier = Modifier.height(18.dp))
                FitnessBodyProfileCard(
                    selectedProfile = fitnessBodyProfile,
                    onProfileSelected = onFitnessBodyProfileSelected
                )
                Spacer(modifier = Modifier.height(18.dp))
                HomePanel(modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.Text(
                        text = "Import en test",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        color = homePalette().textPrimary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    androidx.compose.material3.Text(
                        text = "Hier staan de tijdelijke import- en synctools, zodat ze niet meer in de hoofdnav zitten.",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = homePalette().textSecondary
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    syncContent()
                }
                Spacer(modifier = Modifier.height(18.dp))
                profileContent()
            }
        )
    }
}
