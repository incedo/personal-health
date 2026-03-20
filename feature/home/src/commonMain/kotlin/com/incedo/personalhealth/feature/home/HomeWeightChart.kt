package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

@Composable
internal fun WeightTimelineCard(
    timeline: HomeWeightTimeline,
    selectedRange: HomeWeightRange,
    zoomLevel: Float,
    onRangeSelected: (HomeWeightRange) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val axis = weightChartAxis(timeline.points)
    var selectedPointIndex by remember(timeline.points) { mutableIntStateOf(timeline.points.indexOfLast { it.weightKg != null }) }
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
            if (selectedPoint != null) {
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
                            text = formatWeightKg(selectedPoint.weightKg),
                            style = MaterialTheme.typography.titleLarge,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = selectedPoint.periodLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                }
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
                Button(
                    onClick = onZoomOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.surface,
                        contentColor = palette.textPrimary
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) { Text("Zoom uit") }
                Button(
                    onClick = onZoomIn,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.surface,
                        contentColor = palette.textPrimary
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) { Text("Zoom in") }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(
                        modifier = Modifier.height(266.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        axis.yAxisLabels.forEach { label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textSecondary,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.width((timeline.points.size * 72 * zoomLevel).dp)) {
                        WeightLineChart(
                            timeline = timeline.points,
                            axis = axis,
                            selectedPointIndex = selectedPointIndex,
                            onPointSelected = { selectedPointIndex = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(266.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            timeline.points.forEach { point ->
                                Column(
                                    modifier = Modifier.width((56 * zoomLevel).dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = point.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.textSecondary
                                    )
                                    Text(
                                        text = point.weightKg?.let(::formatWeightKg) ?: "Geen",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
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
private fun WeightLineChart(
    timeline: List<WeightTimelinePoint>,
    axis: WeightChartAxis,
    selectedPointIndex: Int,
    onPointSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val range = (axis.maxWeightKg - axis.minWeightKg).takeIf { it > 0.0 } ?: 1.0

    Canvas(
        modifier = modifier.pointerInput(timeline, selectedPointIndex) {
            detectNearestPointSelection(
                timeline = timeline,
                axis = axis,
                selectedPointIndex = selectedPointIndex,
                onPointSelected = onPointSelected,
                width = size.width.toFloat(),
                height = size.height.toFloat()
            )
        }
    ) {
        repeat(4) { index ->
            val y = size.height * index / 3f
            drawLine(
                color = palette.surfaceMuted.copy(alpha = 0.7f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2f
            )
        }

        val plottedPoints = timeline.mapIndexedNotNull { index, point ->
            val weight = point.weightKg ?: return@mapIndexedNotNull null
            val stepX = if (timeline.size <= 1) size.width / 2f else size.width * index / (timeline.size - 1)
            val ratio = ((weight - axis.minWeightKg) / range).toFloat()
            val y = size.height - (ratio * (size.height * 0.82f)) - 12f
            index to Offset(stepX, y)
        }

        if (plottedPoints.isNotEmpty()) {
            val path = Path().apply {
                moveTo(plottedPoints.first().second.x, plottedPoints.first().second.y)
                plottedPoints.drop(1).forEach { (_, point) ->
                    lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = path,
                color = palette.accent,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            plottedPoints.forEach { (index, point) ->
                drawCircle(
                    color = if (index == selectedPointIndex) Color.White else palette.accent,
                    radius = if (index == selectedPointIndex) 10f else 7f,
                    center = point
                )
                drawCircle(
                    color = palette.accent,
                    radius = if (index == selectedPointIndex) 5f else 3.5f,
                    center = point
                )
            }
        }
    }
}

private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectNearestPointSelection(
    timeline: List<WeightTimelinePoint>,
    axis: WeightChartAxis,
    selectedPointIndex: Int,
    onPointSelected: (Int) -> Unit,
    width: Float,
    height: Float
) {
    if (timeline.isEmpty()) return
    detectTapGestures { tapOffset ->
        val plottedPoints = weightChartOffsets(timeline, axis, width, height)
        val nearest = plottedPoints.minByOrNull { (_, offset) ->
            hypot((offset.x - tapOffset.x).toDouble(), (offset.y - tapOffset.y).toDouble())
        } ?: return@detectTapGestures
        val distance = hypot((nearest.second.x - tapOffset.x).toDouble(), (nearest.second.y - tapOffset.y).toDouble())
        if (distance <= 28.0 && nearest.first != selectedPointIndex) {
            onPointSelected(nearest.first)
        }
    }
}

private fun weightChartOffsets(
    timeline: List<WeightTimelinePoint>,
    axis: WeightChartAxis,
    width: Float,
    height: Float
): List<Pair<Int, Offset>> {
    val range = (axis.maxWeightKg - axis.minWeightKg).takeIf { it > 0.0 } ?: 1.0
    return timeline.mapIndexedNotNull { index, point ->
        val weight = point.weightKg ?: return@mapIndexedNotNull null
        val stepX = if (timeline.size <= 1) width / 2f else width * index / (timeline.size - 1)
        val ratio = ((weight - axis.minWeightKg) / range).toFloat()
        val y = height - (ratio * (height * 0.82f)) - 12f
        index to Offset(stepX, y)
    }
}
