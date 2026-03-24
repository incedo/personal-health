package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.LineChartColors
import com.incedo.personalhealth.core.designsystem.LineChartPoint
import com.incedo.personalhealth.core.designsystem.LineChartTimeline

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
    var zoomLevel by remember(timeline) { mutableFloatStateOf(MinChartZoomLevel) }
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
                    HomeChartZoomControls(
                        onZoomOut = { zoomLevel = nextChartZoomLevel(zoomLevel, zoomIn = false) },
                        onZoomIn = { zoomLevel = nextChartZoomLevel(zoomLevel, zoomIn = true) }
                    )
                    LineChartTimeline(
                        points = timeline.map { LineChartPoint(label = it.label, value = it.bpm.toDouble()) },
                        axis = heartRateChartAxis(timeline),
                        colors = LineChartColors(
                            line = palette.warning,
                            point = palette.warning,
                            selectedOuterPoint = palette.warning,
                            selectedInnerPoint = palette.warning,
                            grid = palette.surfaceMuted.copy(alpha = 0.7f),
                            axisLabel = palette.textSecondary,
                            bottomLabel = palette.textSecondary,
                            bottomValue = palette.textPrimary
                        ),
                        chartHeight = 164.dp,
                        pointWidth = 56.dp,
                        zoomLevel = zoomLevel,
                        minZoomLevel = MinChartZoomLevel,
                        maxZoomLevel = MaxChartZoomLevel,
                        onZoomLevelChange = { zoomLevel = it },
                        valueLabel = { point ->
                            "${point.value?.toInt() ?: 0}"
                        }
                    )
                }
            }
        }
    }
}
