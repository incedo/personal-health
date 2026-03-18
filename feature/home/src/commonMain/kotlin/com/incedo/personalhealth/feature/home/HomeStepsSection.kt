package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun StepDetailScreen(
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    onBack: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    val stats = stepDetailStats(stepsTimeline)
    val spacing = if (compact) 14.dp else 18.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        HomeHeroCard(
            eyebrow = "Steps",
            title = "Stappen van vandaag",
            subtitle = "Bekijk het totaal, je piekuur en hoe je stappen door de dag heen zijn verdeeld.",
            accent = palette.accent,
            compact = compact,
            sideContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatSteps(steps),
                        style = MaterialTheme.typography.displaySmall,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "stappen totaal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }
        )

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.surfaceRaised,
                contentColor = palette.textPrimary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Terug naar home")
        }

        HomePanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Stappen",
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Totaal vandaag: ${formatSteps(stats.totalSteps)} stappen",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Piekmoment: ${stats.peakHourLabel} met ${formatSteps(stats.peakHourSteps)} stappen",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Actieve uren: ${stats.activeHours}",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(18.dp))
            StepsTodayGraphCard(
                title = "Stappen per uur",
                subtitle = "Dagverdeling",
                stepsTimeline = stepsTimeline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun StepsTodayGraphCard(
    title: String,
    subtitle: String,
    stepsTimeline: List<StepTimelinePoint>,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val maxValue = (stepsTimeline.maxOfOrNull { it.steps } ?: 0).coerceAtLeast(1)
    val chartHeight = 164.dp
    val barWidth = 14.dp
    val barSpacing = 8.dp
    val axisLabels = listOf(0, 6, 12, 18, 23)
    Surface(
        modifier = modifier,
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Totaal ${formatSteps(stepsTimeline.sumOf { it.steps })}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary
                    )
                }
            }
            Surface(
                color = palette.surface,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val chartWidth = (stepsTimeline.size * barWidth.value + (stepsTimeline.size - 1) * barSpacing.value).dp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier.width(chartWidth)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(chartHeight)
                            ) {
                                StepChartGrid(
                                    modifier = Modifier.matchParentSize(),
                                    lineColor = palette.surfaceMuted.copy(alpha = 0.65f)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart),
                                    horizontalArrangement = Arrangement.spacedBy(barSpacing),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    stepsTimeline.forEach { point ->
                                        val ratio = (point.steps.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .width(barWidth)
                                                .height((chartHeight * ratio).coerceAtLeast(6.dp))
                                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(palette.accent, palette.accentSoft)
                                                    )
                                                )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                axisLabels.forEach { hour ->
                                    Text(
                                        text = "${hour}u",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepChartGrid(
    modifier: Modifier = Modifier,
    lineColor: Color
) {
    Canvas(modifier = modifier) {
        val rowCount = 4
        val strokeWidth = 1.dp.toPx()
        repeat(rowCount) { index ->
            val y = size.height * (index.toFloat() / (rowCount - 1).toFloat())
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth
            )
        }
    }
}
