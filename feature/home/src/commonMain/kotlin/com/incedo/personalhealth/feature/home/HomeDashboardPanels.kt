package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhBars
import com.incedo.personalhealth.core.designsystem.PhCard
import com.incedo.personalhealth.core.designsystem.PhHeatmap
import com.incedo.personalhealth.core.designsystem.PhListRow
import com.incedo.personalhealth.core.designsystem.PhSectionHeader
import com.incedo.personalhealth.core.designsystem.PhTag
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.designsystem.PhTheme
import com.incedo.personalhealth.core.designsystem.PhZoneBar
import com.incedo.personalhealth.core.recommendations.DailyRecommendation

@Composable
internal fun DashboardPanelGrid(
    expanded: Boolean,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    weightSummary: String,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    dailyRecommendation: DailyRecommendation,
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityClockEpochMillis: Long
) {
    val panels = listOf<@Composable (Modifier) -> Unit>(
        { modifier -> WeeklyVolumePanel(activityMinutesToday, stepsTimeline, modifier) },
        { modifier -> SleepPanel(heartRateBpm, modifier) },
        { modifier -> PlanPanel(dailyRecommendation, modifier) },
        { modifier -> MuscleBalancePanel(activityOptions, modifier) },
        { modifier -> BodyTrendPanel(weightSummary, steps, modifier) },
        { modifier -> RecentSessionsPanel(activeActivity, activityClockEpochMillis, activityOptions, modifier) },
        { modifier -> ConsistencyPanel(stepsTimeline, modifier) }
    )

    if (expanded) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            panels.chunked(2).forEach { rowPanels ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    rowPanels.forEach { panel -> panel(Modifier.weight(1f)) }
                    if (rowPanels.size == 1) Box(modifier = Modifier.weight(1f))
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            panels.forEach { panel -> panel(Modifier.fillMaxWidth()) }
        }
    }
}

@Composable
private fun WeeklyVolumePanel(
    activityMinutesToday: Int,
    stepsTimeline: List<StepTimelinePoint>,
    modifier: Modifier
) {
    PhCard(modifier = modifier) {
        PhSectionHeader(kicker = "Load", title = "Weekly volume")
        Text(
            text = "$activityMinutesToday actieve minuten vandaag",
            style = PhTheme.typography.bodySmall,
            color = PhTheme.colors.textMuted,
            modifier = Modifier.padding(top = PhTheme.spacing.sm)
        )
        PhBars(
            data = weeklyVolumeBars(activityMinutesToday, stepsTimeline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PhTheme.spacing.lg)
                .height(96.dp)
        )
    }
}

@Composable
private fun SleepPanel(
    heartRateBpm: Int,
    modifier: Modifier
) {
    val score = sleepRecoveryScore(heartRateBpm)
    PhCard(modifier = modifier) {
        PhSectionHeader(kicker = "Recovery", title = "Sleep panel") {
            PhTag(text = "$score%", tone = if (score >= 75) PhTagTone.Success else PhTagTone.Warning)
        }
        Text(
            text = if (score >= 75) {
                "Herstel oogt stabiel; houd je avondritme rustig."
            } else {
                "Plan extra rust en vermijd een late intensieve sessie."
            },
            style = PhTheme.typography.body,
            color = PhTheme.colors.text,
            modifier = Modifier.padding(top = PhTheme.spacing.lg)
        )
        PhZoneBar(
            zones = listOf(score.toFloat(), (100 - score).toFloat()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PhTheme.spacing.lg)
        )
    }
}

@Composable
private fun PlanPanel(
    dailyRecommendation: DailyRecommendation,
    modifier: Modifier
) {
    PhCard(modifier = modifier, raised = true) {
        PhSectionHeader(kicker = "Plan", title = dailyRecommendation.title)
        Text(
            text = dailyRecommendation.guidance,
            style = PhTheme.typography.body,
            color = PhTheme.colors.text,
            modifier = Modifier.padding(top = PhTheme.spacing.md)
        )
        dailyRecommendation.insights.firstOrNull()?.let { insight ->
            PhTag(
                text = insight.title,
                tone = PhTagTone.Primary,
                modifier = Modifier.padding(top = PhTheme.spacing.lg)
            )
        }
    }
}

@Composable
private fun MuscleBalancePanel(
    activityOptions: List<QuickActivityType>,
    modifier: Modifier
) {
    val groups = muscleBalanceGroups(activityOptions)
    PhCard(modifier = modifier) {
        PhSectionHeader(kicker = "Strength", title = "Muscle balance")
        groups.forEach { group ->
            PhListRow(
                title = group.label,
                meta = group.focus,
                value = "${group.load}%",
                accent = group.color,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BodyTrendPanel(
    weightSummary: String,
    steps: Int,
    modifier: Modifier
) {
    PhCard(modifier = modifier) {
        PhSectionHeader(kicker = "Body", title = "Body trend")
        Text(
            text = weightSummary,
            style = PhTheme.typography.metric,
            color = PhTheme.colors.text,
            modifier = Modifier.padding(top = PhTheme.spacing.sm)
        )
        Text(
            text = "Beweging vandaag: ${formatSteps(steps)} stappen",
            style = PhTheme.typography.bodySmall,
            color = PhTheme.colors.textMuted
        )
    }
}

@Composable
private fun RecentSessionsPanel(
    activeActivity: ActiveQuickActivitySession?,
    nowEpochMillis: Long,
    activityOptions: List<QuickActivityType>,
    modifier: Modifier
) {
    PhCard(modifier = modifier) {
        PhSectionHeader(kicker = "Log", title = "Recent sessions")
        if (activeActivity != null) {
            PhListRow(
                title = "${activeActivity.type.label} bezig",
                meta = formatActiveDuration(activeActivity.elapsedDurationMillis(nowEpochMillis)),
                value = "Live",
                accent = PhTheme.colors.primary
            )
        }
        activityOptions.filter { it != activeActivity?.type }.take(3).forEach { option ->
            PhListRow(
                title = option.label,
                meta = "Klaar om te loggen",
                value = "Plan",
                accent = PhTheme.colors.accent
            )
        }
    }
}

@Composable
private fun ConsistencyPanel(
    stepsTimeline: List<StepTimelinePoint>,
    modifier: Modifier
) {
    PhCard(modifier = modifier) {
        PhSectionHeader(kicker = "Rhythm", title = "Consistency")
        Text(
            text = "${activeStepBuckets(stepsTimeline)} actieve blokken vandaag",
            style = PhTheme.typography.bodySmall,
            color = PhTheme.colors.textMuted,
            modifier = Modifier.padding(top = PhTheme.spacing.sm)
        )
        Box(modifier = Modifier.padding(top = PhTheme.spacing.lg), contentAlignment = Alignment.CenterStart) {
            PhHeatmap(data = consistencyHeatmap(stepsTimeline))
        }
    }
}

internal fun weeklyVolumeBars(activityMinutesToday: Int, stepsTimeline: List<StepTimelinePoint>): List<Float> {
    val base = (stepsTimeline.sumOf { it.steps } / 1000f).coerceAtLeast(1f)
    return listOf(0.7f, 0.82f, 0.62f, 0.95f, 0.74f, 0.88f).map { it * base } + activityMinutesToday.toFloat().coerceAtLeast(1f)
}

internal fun sleepRecoveryScore(heartRateBpm: Int): Int =
    (92 - (heartRateBpm - 56).coerceAtLeast(0)).coerceIn(48, 92)

internal fun activeStepBuckets(stepsTimeline: List<StepTimelinePoint>): Int =
    stepsTimeline.count { it.steps > 0 }

internal fun consistencyHeatmap(stepsTimeline: List<StepTimelinePoint>): List<Float> {
    val values = stepsTimeline.map { (it.steps / 900f).coerceIn(0f, 1f) }
    return List(84) { index -> values.getOrNull(index % values.size.coerceAtLeast(1)) ?: 0f }
}

private data class MuscleBalanceGroup(
    val label: String,
    val focus: String,
    val load: Int,
    val color: Color
)

@Composable
private fun muscleBalanceGroups(activityOptions: List<QuickActivityType>): List<MuscleBalanceGroup> {
    val hasFitness = QuickActivityType.FITNESS in activityOptions
    return listOf(
        MuscleBalanceGroup("Lower body", "Walk, run, cycle", if (hasFitness) 72 else 64, PhTheme.colors.primary),
        MuscleBalanceGroup("Core", "Stability", if (hasFitness) 58 else 44, PhTheme.colors.warning),
        MuscleBalanceGroup("Upper body", "Strength", if (hasFitness) 46 else 32, PhTheme.colors.info)
    )
}
