package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.LineChartColors
import com.incedo.personalhealth.core.designsystem.LineChartPoint
import com.incedo.personalhealth.core.designsystem.LineChartTimeline

@Composable
internal fun WeightTimelineCard(
    timeline: HomeWeightTimeline,
    selectedRange: HomeWeightRange,
    zoomLevel: Float,
    onRangeSelected: (HomeWeightRange) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomLevelChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val chartPoints = timeline.points.map { LineChartPoint(label = it.label, value = it.weightKg) }
    val axis = weightChartAxis(timeline.points)
    var selectedPointIndex by remember(timeline.points) {
        mutableIntStateOf(timeline.points.indexOfLast { it.weightKg != null })
    }
    val selectedPoint = timeline.points.getOrNull(selectedPointIndex)?.takeIf { it.weightKg != null }

    LaunchedEffect(timeline.points) {
        selectedPointIndex = timeline.points.indexOfLast { it.weightKg != null }
    }

    Surface(
        modifier = modifier,
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Tijdslijn",
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = timeline.title,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            selectedPoint?.let { point ->
                SelectedWeightSummary(point = point)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HomeWeightRange.entries.forEach { range ->
                    FilterChip(
                        selected = range == selectedRange,
                        onClick = { onRangeSelected(range) },
                        label = { Text(range.label) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeChartZoomControls(
                    onZoomOut = onZoomOut,
                    onZoomIn = onZoomIn
                )
            }
            LineChartTimeline(
                points = chartPoints,
                axis = axis,
                colors = LineChartColors(
                    line = palette.accent,
                    point = palette.accent,
                    selectedOuterPoint = Color.White,
                    selectedInnerPoint = palette.accent,
                    grid = palette.surfaceMuted.copy(alpha = 0.7f),
                    axisLabel = palette.textSecondary,
                    bottomLabel = palette.textSecondary,
                    bottomValue = palette.textPrimary
                ),
                zoomLevel = zoomLevel,
                minZoomLevel = MinChartZoomLevel,
                maxZoomLevel = MaxChartZoomLevel,
                selectedPointIndex = selectedPointIndex.takeIf { it >= 0 },
                onPointSelected = { selectedPointIndex = it },
                onZoomLevelChange = onZoomLevelChange,
                valueLabel = { point -> point.value?.let(::formatWeightKg) ?: "Geen" }
            )
        }
    }
}

@Composable
private fun SelectedWeightSummary(point: WeightTimelinePoint) {
    val palette = homePalette()
    Surface(
        color = palette.surface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Gemiddeld gewicht",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary
            )
            Text(
                text = formatWeightKg(point.weightKg),
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = point.periodLabel,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
        }
    }
}
