package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhSegmentedControl
import com.incedo.personalhealth.core.designsystem.PhSegmentedOption
import com.incedo.personalhealth.core.designsystem.PhTheme
import com.incedo.personalhealth.core.recommendations.DailyRecommendation

@Composable
internal fun TodayDashboardContent(
    expanded: Boolean,
    compact: Boolean,
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    weightSummary: String,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    dailyRecommendation: DailyRecommendation,
    profileName: String,
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityClockEpochMillis: Long,
    onStartActivity: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onLogNutrition: () -> Unit,
    onOpenStepsDetail: () -> Unit,
    onOpenHeartRateDetail: () -> Unit,
    onOpenWeightDetail: () -> Unit,
    onOpenHealthDataDetail: () -> Unit
) {
    val spacing = if (compact) PhTheme.spacing.lg else PhTheme.spacing.xl
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing)) {
        TodayHeader(compact = compact)
        TodayReadinessHero(
            fitScore = fitScore,
            steps = steps,
            activityMinutesToday = activityMinutesToday,
            heartRateBpm = heartRateBpm,
            dailyRecommendation = dailyRecommendation,
            profileName = profileName,
            compact = compact,
            activeActivity = activeActivity,
            activityClockEpochMillis = activityClockEpochMillis,
            activityOptions = activityOptions,
            onStartActivity = onStartActivity,
            onStopActivity = onStopActivity,
            onLogNutrition = onLogNutrition,
            onOpenHealthDataDetail = onOpenHealthDataDetail
        )
        TodayMetricRow(
            expanded = expanded,
            stepsTimeline = stepsTimeline,
            heartRateBpm = heartRateBpm,
            onOpenHeartRateDetail = onOpenHeartRateDetail,
            onOpenHealthDataDetail = onOpenHealthDataDetail
        )
        TodayPrimaryPanels(
            expanded = expanded,
            steps = steps,
            stepsTimeline = stepsTimeline,
            activityMinutesToday = activityMinutesToday,
            dailyRecommendation = dailyRecommendation,
            weightSummary = weightSummary,
            onOpenStepsDetail = onOpenStepsDetail
        )
    }
}

@Composable
private fun TodayHeader(compact: Boolean) {
    var period by rememberSaveable { mutableStateOf("week") }
    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.md)) {
            TodayTitleBlock()
            PhSegmentedControl(todayPeriods(), period, { period = it })
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.xxxl),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TodayTitleBlock()
            PhSegmentedControl(todayPeriods(), period, { period = it })
        }
    }
}

@Composable
private fun TodayTitleBlock() {
    Column(verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.xs)) {
        Text("Vandaag", style = PhTheme.typography.bodySmall, color = PhTheme.colors.textMuted)
        Text("Vandaag", style = PhTheme.typography.display, color = PhTheme.colors.text, fontWeight = FontWeight.SemiBold)
    }
}

private fun todayPeriods(): List<PhSegmentedOption> = listOf(
    PhSegmentedOption("day", "Dag"),
    PhSegmentedOption("week", "Week"),
    PhSegmentedOption("month", "Maand")
)

@Composable
private fun TodayReadinessHero(
    fitScore: Int,
    steps: Int,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    dailyRecommendation: DailyRecommendation,
    profileName: String,
    compact: Boolean,
    activeActivity: ActiveQuickActivitySession?,
    activityClockEpochMillis: Long,
    activityOptions: List<QuickActivityType>,
    onStartActivity: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onLogNutrition: () -> Unit,
    onOpenHealthDataDetail: () -> Unit
) {
    val palette = homePalette()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PhTheme.shapes.xl,
        tonalElevation = PhTheme.elevation.sm,
        shadowElevation = PhTheme.elevation.sm,
        border = BorderStroke(1.dp, PhTheme.colors.divider),
        color = PhTheme.colors.surface
    ) {
        val heroModifier = Modifier
            .background(Brush.horizontalGradient(listOf(palette.accentSoft.copy(alpha = 0.5f), PhTheme.colors.surface)))
            .padding(if (compact) PhTheme.spacing.xl else PhTheme.spacing.xxxl)
        if (compact) {
            Column(modifier = heroModifier, verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.lg)) {
                TodayRingCluster(fitScore, steps, activityMinutesToday, heartRateBpm, onOpenHealthDataDetail, true)
                TodayHeroCopy(dailyRecommendation, profileName)
                TodayHeroActions(activityOptions, activeActivity, activityClockEpochMillis, onStartActivity, onStopActivity, onLogNutrition)
            }
        } else {
            Row(
                modifier = heroModifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.xxxl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TodayRingCluster(fitScore, steps, activityMinutesToday, heartRateBpm, onOpenHealthDataDetail, false)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.lg)) {
                    TodayHeroCopy(dailyRecommendation, profileName)
                    TodayHeroActions(activityOptions, activeActivity, activityClockEpochMillis, onStartActivity, onStopActivity, onLogNutrition)
                }
            }
        }
    }
}

@Composable
private fun TodayRingCluster(
    fitScore: Int,
    steps: Int,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    onOpenHealthDataDetail: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    val ringSize = if (compact) 150.dp else 176.dp
    HomeRingCluster(
        progressValues = readinessRingProgressValues(fitScore, steps, activityMinutesToday, heartRateBpm),
        colors = listOf(palette.accent, palette.warm, palette.warning),
        trackColors = listOf(palette.accentSoft, palette.warmSoft, palette.warningSoft).map { it.copy(alpha = 0.7f) },
        modifier = Modifier.size(ringSize),
        onClick = onOpenHealthDataDetail,
        centerSizeFraction = 0.48f,
        strokeWidthPx = if (compact) 12f else 14f,
        ringGapPx = 6f,
        centerState = HomeRingCenterState(
            heartProgress = fitScore.coerceIn(0, 100) / 100f,
            scoreText = fitScore.coerceIn(0, 100).toString(),
            heartSizeFraction = 0.78f,
            scoreTextStyle = PhTheme.typography.h2
        )
    )
}

internal fun readinessRingProgressValues(fitScore: Int, steps: Int, activityMinutesToday: Int, heartRateBpm: Int): List<Float> =
    listOf(
        fitScore.coerceIn(0, 100) / 100f,
        (steps / 8000f).coerceIn(0f, 1f),
        ((86 - heartRateBpm).coerceIn(0, 30) / 30f + activityMinutesToday / 180f).coerceIn(0f, 1f)
    )

@Composable
private fun TodayHeroCopy(dailyRecommendation: DailyRecommendation, profileName: String) {
    Column(verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.sm)) {
        Text("Goed hersteld", style = PhTheme.typography.h1, color = PhTheme.colors.text, fontWeight = FontWeight.SemiBold)
        Text("Welkom terug, $profileName. ${dailyRecommendation.summary}", style = PhTheme.typography.body, color = PhTheme.colors.textMuted)
        Text(dailyRecommendation.guidance, style = PhTheme.typography.body, color = PhTheme.colors.text)
    }
}

@Composable
private fun TodayHeroActions(
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityClockEpochMillis: Long,
    onStartActivity: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onLogNutrition: () -> Unit
) {
    if (activeActivity != null) {
        ActiveActivityCard(activeActivity, activityClockEpochMillis, onStopActivity)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.md)) {
        listOf(QuickActivityType.RUNNING, QuickActivityType.FITNESS, QuickActivityType.NUTRITION).forEach { type ->
            val enabled = type in activityOptions || type == QuickActivityType.NUTRITION
            val click = if (type == QuickActivityType.NUTRITION) onLogNutrition else { { onStartActivity(type) } }
            TodayActionChip(type, enabled, click)
        }
    }
}

@Composable
private fun TodayActionChip(type: QuickActivityType, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clip(PhTheme.shapes.pill).clickable(enabled = enabled, onClick = onClick),
        shape = PhTheme.shapes.pill,
        color = if (type == QuickActivityType.RUNNING) PhTheme.colors.primary else PhTheme.colors.surfaceMuted
    ) {
        Row(
            modifier = Modifier.padding(horizontal = PhTheme.spacing.lg, vertical = PhTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickActivityIcon(type, if (type == QuickActivityType.RUNNING) PhTheme.colors.onPrimary else PhTheme.colors.text, Modifier.size(18.dp))
            Text(type.todayLabel(), style = PhTheme.typography.button, color = if (type == QuickActivityType.RUNNING) PhTheme.colors.onPrimary else PhTheme.colors.text)
        }
    }
}

private fun QuickActivityType.todayLabel(): String = when (this) {
    QuickActivityType.RUNNING -> "Run"
    QuickActivityType.FITNESS -> "Workout"
    QuickActivityType.NUTRITION -> "Meal"
    else -> label
}
