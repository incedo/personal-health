package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhButton
import com.incedo.personalhealth.core.designsystem.PhButtonVariant
import com.incedo.personalhealth.core.designsystem.PhCard
import com.incedo.personalhealth.core.designsystem.PhMetricCard
import com.incedo.personalhealth.core.designsystem.PhSparkline
import com.incedo.personalhealth.core.designsystem.PhTag
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun ProgressPlaceholderContent(
    fitScore: Int,
    steps: Int,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    healthMetricCards: List<HomeHealthMetricCard>,
    compact: Boolean,
    onOpenHealthDataDetail: () -> Unit
) {
    val weightSummary = resolveHealthMetricValue(
        metrics = healthMetricCards,
        metricId = BODY_WEIGHT_HEALTH_METRIC_ID
    )
    HomeSectionScreen(
        tab = HomeTab.PROGRESS,
        compact = compact,
        leadingContent = {
            HomeHeroCard(
                eyebrow = "Progress",
                title = "Trends in opbouw",
                subtitle = "Hier komt je voortgang samen: readiness, training load, lichaamstrends en gedrag over tijd.",
                accent = homePalette().accent,
                compact = compact,
                sideContent = {
                    HomeStatusBadge(
                        label = "Score",
                        value = fitScore.toString()
                    )
                }
            )
        },
        bodyContent = {
            ProgressMetricGrid(
                compact = compact,
                fitScore = fitScore,
                steps = steps,
                activityMinutesToday = activityMinutesToday,
                heartRateBpm = heartRateBpm,
                weightSummary = weightSummary,
                onOpenHealthDataDetail = onOpenHealthDataDetail
            )
            ProgressPlaceholderCard()
        }
    )
}

@Composable
private fun ProgressMetricGrid(
    compact: Boolean,
    fitScore: Int,
    steps: Int,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    weightSummary: String,
    onOpenHealthDataDetail: () -> Unit
) {
    val cards = listOf<@Composable (Modifier) -> Unit>(
        { modifier ->
            PhMetricCard(
                label = "Readiness",
                value = fitScore.toString(),
                unit = "/100",
                trend = "Vandaag",
                tone = PhTagTone.Primary,
                chart = { PhSparkline(listOf(62f, 68f, 65f, 72f, fitScore.toFloat()), Modifier.fillMaxWidth()) },
                modifier = modifier.clickable(onClick = onOpenHealthDataDetail)
            )
        },
        { modifier ->
            PhMetricCard(
                label = "Steps",
                value = formatSteps(steps),
                trend = "Dagvolume",
                tone = PhTagTone.Success,
                modifier = modifier
            )
        },
        { modifier ->
            PhMetricCard(
                label = "Load",
                value = activityMinutesToday.toString(),
                unit = "min",
                trend = "Activity",
                tone = PhTagTone.Warning,
                modifier = modifier
            )
        },
        { modifier ->
            PhMetricCard(
                label = "Heart rate",
                value = heartRateBpm.toString(),
                unit = "bpm",
                trend = "Recovery",
                tone = PhTagTone.Danger,
                modifier = modifier
            )
        },
        { modifier ->
            PhMetricCard(
                label = "Body",
                value = weightSummary,
                trend = "Trend",
                tone = PhTagTone.Info,
                modifier = modifier
            )
        }
    )
    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            cards.forEach { card -> card(Modifier.fillMaxWidth()) }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            cards.chunked(3).forEach { rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    rowCards.forEach { card -> card(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun ProgressPlaceholderCard() {
    PhCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PhTag(text = "Placeholder", tone = PhTagTone.Info)
            Text(
                text = "Progress krijgt later de echte trendmodules",
                style = PhTheme.typography.h3,
                color = PhTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Deze view is alvast aangesloten op de nieuwe navigatie, zodat mobile, tablet en desktop dezelfde hoofdstructuur gebruiken.",
                style = PhTheme.typography.bodySmall,
                color = PhTheme.colors.textMuted
            )
            PhButton(
                text = "Open health data",
                onClick = {},
                variant = PhButtonVariant.Secondary,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
