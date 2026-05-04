package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.incedo.personalhealth.core.designsystem.PhButton
import com.incedo.personalhealth.core.designsystem.PhButtonVariant
import com.incedo.personalhealth.core.designsystem.PhCard
import com.incedo.personalhealth.core.designsystem.PhSparkline
import com.incedo.personalhealth.core.designsystem.PhTag
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.designsystem.PhTheme
import com.incedo.personalhealth.core.designsystem.PhZoneBar
import com.incedo.personalhealth.core.recommendations.DailyRecommendation

@Composable
internal fun TodayMetricRow(
    expanded: Boolean,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    onOpenHeartRateDetail: () -> Unit,
    onOpenHealthDataDetail: () -> Unit
) {
    val hrvData = stepsTimeline.map { it.steps.toFloat() / 100f + 48f }.ifEmpty { listOf(58f, 61f, 64f, 68f) }
    val sleepData = listOf(6.4f, 6.9f, 6.7f, 7.2f, 7.0f, 7.1f, 7.7f)
    TodayTwoColumn(
        expanded = expanded,
        left = { TodayMetricCard("HRV", heartRateBpm.toString(), "ms", "+4 %", hrvData, onOpenHeartRateDetail) },
        right = { TodayMetricCard("Slaap", "7u 42m", null, "+2 %", sleepData, onOpenHealthDataDetail, PhTheme.colors.info) }
    )
}

@Composable
private fun TodayMetricCard(
    label: String,
    value: String,
    unit: String?,
    trend: String,
    data: List<Float>,
    onClick: () -> Unit,
    lineColor: Color = PhTheme.colors.primary
) {
    PhCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), padding = PhTheme.spacing.xl) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            PhTag(label.take(1), tone = PhTagTone.Primary)
            PhTag(trend, tone = PhTagTone.Success)
        }
        Spacer(Modifier.height(PhTheme.spacing.xxl))
        Text(label.uppercase(), style = PhTheme.typography.label, color = PhTheme.colors.textMuted)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = PhTheme.typography.metric, color = PhTheme.colors.text, fontWeight = FontWeight.SemiBold)
            unit?.let {
                Text(
                    " $it",
                    style = PhTheme.typography.body,
                    color = PhTheme.colors.textMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        PhSparkline(data, modifier = Modifier.fillMaxWidth().padding(top = PhTheme.spacing.xl), lineColor = lineColor)
    }
}

@Composable
internal fun TodayPrimaryPanels(
    expanded: Boolean,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    activityMinutesToday: Int,
    dailyRecommendation: DailyRecommendation,
    weightSummary: String,
    onOpenStepsDetail: () -> Unit
) {
    TodayTwoColumn(
        expanded = expanded,
        left = { TodayRecommendationPanel(dailyRecommendation) },
        right = { TodayWeekPanel(steps, stepsTimeline, activityMinutesToday, weightSummary, onOpenStepsDetail) }
    )
}

@Composable
private fun TodayRecommendationPanel(dailyRecommendation: DailyRecommendation) {
    PhCard(modifier = Modifier.fillMaxWidth(), raised = true, padding = PhTheme.spacing.xxl) {
        PhTag("Vandaag aanbevolen", tone = PhTagTone.Primary)
        Spacer(Modifier.height(PhTheme.spacing.xl))
        Text(dailyRecommendation.title, style = PhTheme.typography.h2, color = PhTheme.colors.text, fontWeight = FontWeight.SemiBold)
        Text(
            dailyRecommendation.guidance,
            style = PhTheme.typography.body,
            color = PhTheme.colors.textMuted,
            modifier = Modifier.padding(top = PhTheme.spacing.md)
        )
        PhZoneBar(
            listOf(1f, 2f, 3f, 2f, 0.2f),
            modifier = Modifier.fillMaxWidth().padding(top = PhTheme.spacing.xxl)
        )
        PhButton(
            "Open plan",
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(top = PhTheme.spacing.xxl),
            variant = PhButtonVariant.Primary
        )
    }
}

@Composable
private fun TodayWeekPanel(
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    activityMinutesToday: Int,
    weightSummary: String,
    onOpenStepsDetail: () -> Unit
) {
    PhCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenStepsDetail), padding = PhTheme.spacing.xxl) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Deze week", style = PhTheme.typography.label, color = PhTheme.colors.textMuted)
                Text("${(activityMinutesToday + 342) / 60}u ${(activityMinutesToday + 342) % 60}m", style = PhTheme.typography.h1, color = PhTheme.colors.text)
            }
            PhTag("+12%", tone = PhTagTone.Success)
        }
        PhBars(
            weeklyVolumeBars(activityMinutesToday, stepsTimeline),
            modifier = Modifier.fillMaxWidth().padding(top = PhTheme.spacing.xxl)
        )
        Row(modifier = Modifier.padding(top = PhTheme.spacing.lg), horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.sm)) {
            PhTag("${(steps / 1000f).toString().take(4)} km lopen", tone = PhTagTone.Neutral)
            PhTag("28 km fiets", tone = PhTagTone.Neutral)
            PhTag(weightSummary, tone = PhTagTone.Neutral)
        }
    }
}

@Composable
private fun TodayTwoColumn(expanded: Boolean, left: @Composable () -> Unit, right: @Composable () -> Unit) {
    if (expanded) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.xl)) {
            Box(Modifier.weight(1f)) { left() }
            Box(Modifier.weight(1f)) { right() }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.lg)) {
            left()
            right()
        }
    }
}
