package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    onLogActivity: (QuickActivityType) -> Unit,
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
        val expanded = maxWidth >= 1080.dp
        val outerPadding = if (compact) 16.dp else 24.dp
        val contentSpacing = if (compact) 14.dp else 20.dp

        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = outerPadding, vertical = 12.dp)
            ) {
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
                            compact = true
                        )

                        HomeDetailDestination.FITNESS -> FitnessActivityDetailScreen(
                            sessions = fitnessSessions,
                            bodyProfile = fitnessBodyProfile,
                            onBack = onCloseDetail,
                            onOpenDebugEditor = onOpenFitnessEditorDebug,
                            onSaveSession = onSaveFitnessSession,
                            compact = true
                        )

                        HomeDetailDestination.FITNESS_EDITOR_DEBUG -> FitnessAnatomyEditorScreen(
                            bodyProfile = fitnessBodyProfile,
                            onBack = onCloseDetail,
                            compact = true
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
                                onLogActivity = onLogActivity,
                                onOpenFitnessDetail = onOpenFitnessDetail,
                                onOpenStepsDetail = onOpenStepsDetail,
                                syncContent = syncContent,
                                profileContent = profileContent,
                                compact = true
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                HomeBottomTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(outerPadding),
                horizontalArrangement = Arrangement.spacedBy(contentSpacing)
            ) {
                HomeSidebar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.width(if (expanded) 220.dp else 184.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    when (activeDetailDestination) {
                        HomeDetailDestination.STEPS -> StepDetailScreen(
                            steps = steps,
                            stepsTimeline = detailStepsTimeline,
                            onBack = onCloseDetail,
                            compact = false
                        )

                        HomeDetailDestination.FITNESS -> FitnessActivityDetailScreen(
                            sessions = fitnessSessions,
                            bodyProfile = fitnessBodyProfile,
                            onBack = onCloseDetail,
                            onOpenDebugEditor = onOpenFitnessEditorDebug,
                            onSaveSession = onSaveFitnessSession,
                            compact = false
                        )

                        HomeDetailDestination.FITNESS_EDITOR_DEBUG -> FitnessAnatomyEditorScreen(
                            bodyProfile = fitnessBodyProfile,
                            onBack = onCloseDetail,
                            compact = false
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
                                onLogActivity = onLogActivity,
                                onOpenFitnessDetail = onOpenFitnessDetail,
                                onOpenStepsDetail = onOpenStepsDetail,
                                syncContent = syncContent,
                                profileContent = profileContent,
                                compact = false
                            )
                        }
                    }
                }
            }
        }
    }
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
    onLogActivity: (QuickActivityType) -> Unit,
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

        HomeTab.SYNC -> HomeSectionScreen(
            tab = HomeTab.SYNC,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Sync overzicht",
                    title = "Integraties en imports",
                    subtitle = "Bekijk je laatste syncstatus en stuur handmatige imports aan wanneer dat nodig is.",
                    accent = homePalette().warm,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Data sources",
                            value = "Actief"
                        )
                    }
                )
            },
            bodyContent = syncContent
        )

        HomeTab.PROFILE -> HomeSectionScreen(
            tab = HomeTab.PROFILE,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Profiel",
                    title = "Jouw basis en voorkeuren",
                    subtitle = "Houd accountinstellingen, units en persoonlijke status op een vaste plek bij elkaar.",
                    accent = homePalette().accent,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Fit score",
                            value = "$fitScore%"
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
                ProfileFitScoreCard(
                    fitScore = fitScore,
                    profileName = profileName,
                    modifier = Modifier.fillMaxWidth()
                )
                profileContent()
            }
        )
    }
}
