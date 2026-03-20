package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun HeartRateDetailScreen(
    heartRateBpm: Int,
    timeline: List<HeartRateTimelinePoint>,
    onBack: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    val stats = heartRateDetailStats(timeline)
    val spacing = if (compact) 14.dp else 18.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        HomeHeroCard(
            eyebrow = "Hartslag",
            title = "Hartslag van vandaag",
            subtitle = "Bekijk je gemiddelde, bandbreedte en het ritme van je dag.",
            accent = palette.warning,
            compact = compact,
            sideContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$heartRateBpm bpm",
                        style = MaterialTheme.typography.displaySmall,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "actueel gemiddelde",
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
                text = "Herstelstatus",
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gemiddeld vandaag: ${stats.averageBpm} bpm",
                style = MaterialTheme.typography.bodyLarge,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Laagste punt: ${stats.minBpm} bpm • hoogste punt: ${stats.maxBpm} bpm",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Indicatie: ${stats.recoveryLabel}",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(18.dp))
            HeartRateTimelineCard(
                title = "Hartslag door de dag",
                subtitle = "Rust- en herstelbeeld",
                timeline = timeline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun HeartRateTimelineCard(
    title: String,
    subtitle: String,
    timeline: List<HeartRateTimelinePoint>,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
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
                        text = "${heartRateDetailStats(timeline).averageBpm} bpm gemiddeld",
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
                    val chartWidth = (timeline.size * 56).dp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column(modifier = Modifier.width(chartWidth)) {
                            HeartRateLineChart(
                                timeline = timeline,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(164.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                timeline.forEach { point ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = point.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = palette.textSecondary
                                        )
                                        Text(
                                            text = "${point.bpm}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = palette.textPrimary,
                                            fontWeight = FontWeight.SemiBold
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
}

@Composable
private fun HeartRateLineChart(
    timeline: List<HeartRateTimelinePoint>,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val minValue = timeline.minOfOrNull { it.bpm } ?: 0
    val maxValue = timeline.maxOfOrNull { it.bpm } ?: 1
    val range = (maxValue - minValue).coerceAtLeast(1)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepX = if (timeline.size <= 1) 0f else size.width / (timeline.size - 1)
            val path = Path()

            timeline.forEachIndexed { index, point ->
                val x = stepX * index
                val ratio = (point.bpm - minValue).toFloat() / range.toFloat()
                val y = size.height - (ratio * (size.height * 0.82f)) - 12f
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                drawCircle(
                    color = palette.warning,
                    radius = 6f,
                    center = Offset(x, y)
                )
            }

            drawPath(
                path = path,
                color = palette.warning,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            repeat(3) { index ->
                val y = size.height * (index + 1) / 4f
                drawLine(
                    color = palette.surfaceMuted.copy(alpha = 0.7f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
            }
        }
    }
}
