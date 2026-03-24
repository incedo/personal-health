package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

data class LineChartPoint(
    val label: String,
    val value: Double?
)

data class LineChartAxis(
    val yAxisLabels: List<String>,
    val minValue: Double,
    val maxValue: Double
)

data class LineChartColors(
    val line: Color,
    val point: Color,
    val selectedOuterPoint: Color,
    val selectedInnerPoint: Color,
    val grid: Color,
    val axisLabel: Color,
    val bottomLabel: Color,
    val bottomValue: Color
)

@Composable
fun LineChartTimeline(
    points: List<LineChartPoint>,
    axis: LineChartAxis,
    colors: LineChartColors,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 266.dp,
    pointWidth: Dp = 56.dp,
    zoomLevel: Float = 1f,
    minZoomLevel: Float = 1f,
    maxZoomLevel: Float = 2.4f,
    selectedPointIndex: Int? = null,
    onPointSelected: ((Int) -> Unit)? = null,
    onZoomLevelChange: ((Float) -> Unit)? = null,
    valueLabel: (LineChartPoint) -> String
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta -> scrollState.dispatchRawDelta(-delta) }
            ),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(
                modifier = Modifier.height(chartHeight),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                axis.yAxisLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.axisLabel,
                        textAlign = TextAlign.End
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.width((points.size * pointWidth.value * zoomLevel).dp)) {
                val chartModifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .let { base ->
                        if (onZoomLevelChange == null) {
                            base
                        } else {
                            base.pointerInput(points, zoomLevel, minZoomLevel, maxZoomLevel) {
                                detectPinchZoom(
                                    zoomLevel = zoomLevel,
                                    minZoomLevel = minZoomLevel,
                                    maxZoomLevel = maxZoomLevel,
                                    onZoomLevelChange = onZoomLevelChange
                                )
                            }
                        }
                    }
                    .let { base ->
                        if (onPointSelected == null || selectedPointIndex == null) {
                            base
                        } else {
                            base.pointerInput(points, selectedPointIndex, axis) {
                                detectNearestPointSelection(
                                    points = points,
                                    axis = axis,
                                    selectedPointIndex = selectedPointIndex,
                                    onPointSelected = onPointSelected,
                                    width = size.width.toFloat(),
                                    height = size.height.toFloat()
                                )
                            }
                        }
                    }
                LineChartCanvas(
                    points = points,
                    axis = axis,
                    colors = colors,
                    selectedPointIndex = selectedPointIndex,
                    modifier = chartModifier
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    points.forEach { point ->
                        Column(
                            modifier = Modifier.width((pointWidth.value * zoomLevel).dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = point.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.bottomLabel,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = valueLabel(point),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.bottomValue,
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

private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectPinchZoom(
    zoomLevel: Float,
    minZoomLevel: Float,
    maxZoomLevel: Float,
    onZoomLevelChange: (Float) -> Unit
) {
    awaitEachGesture {
        var currentZoom = zoomLevel
        var keepTracking = true
        while (keepTracking) {
            val event = awaitPointerEvent()
            val pressedPointers = event.changes.count { it.pressed }
            if (pressedPointers > 1) {
                val zoomChange = event.calculateZoom()
                if (!zoomChange.isNaN() && zoomChange != 1f) {
                    val nextZoom = (currentZoom * zoomChange).coerceIn(minZoomLevel, maxZoomLevel)
                    if (nextZoom != currentZoom) {
                        currentZoom = nextZoom
                        onZoomLevelChange(nextZoom)
                    }
                }
                event.changes.forEach { change ->
                    if (change.pressed) change.consume()
                }
            }
            keepTracking = event.changes.any { it.pressed }
        }
    }
}

@Composable
private fun LineChartCanvas(
    points: List<LineChartPoint>,
    axis: LineChartAxis,
    colors: LineChartColors,
    selectedPointIndex: Int?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val plottedPoints = chartOffsets(points, axis, size.width, size.height)
        if (axis.yAxisLabels.size > 1) {
            repeat(axis.yAxisLabels.size) { index ->
                val y = size.height * index / (axis.yAxisLabels.lastIndex.coerceAtLeast(1))
                drawLine(
                    color = colors.grid,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
            }
        }
        if (plottedPoints.isEmpty()) return@Canvas

        val path = Path().apply {
            moveTo(plottedPoints.first().second.x, plottedPoints.first().second.y)
            plottedPoints.drop(1).forEach { (_, point) -> lineTo(point.x, point.y) }
        }
        drawPath(
            path = path,
            color = colors.line,
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )
        plottedPoints.forEach { (index, point) ->
            if (index == selectedPointIndex) {
                drawCircle(color = colors.selectedOuterPoint, radius = 10f, center = point)
                drawCircle(color = colors.selectedInnerPoint, radius = 5f, center = point)
            } else {
                drawCircle(color = colors.point, radius = 7f, center = point)
                drawCircle(color = colors.selectedInnerPoint, radius = 3.5f, center = point)
            }
        }
    }
}

private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectNearestPointSelection(
    points: List<LineChartPoint>,
    axis: LineChartAxis,
    selectedPointIndex: Int,
    onPointSelected: (Int) -> Unit,
    width: Float,
    height: Float
) {
    if (points.isEmpty()) return
    detectTapGestures { tapOffset ->
        val nearest = chartOffsets(points, axis, width, height).minByOrNull { (_, offset) ->
            hypot((offset.x - tapOffset.x).toDouble(), (offset.y - tapOffset.y).toDouble())
        } ?: return@detectTapGestures
        val distance = hypot((nearest.second.x - tapOffset.x).toDouble(), (nearest.second.y - tapOffset.y).toDouble())
        if (distance <= 28.0 && nearest.first != selectedPointIndex) {
            onPointSelected(nearest.first)
        }
    }
}

private fun chartOffsets(
    points: List<LineChartPoint>,
    axis: LineChartAxis,
    width: Float,
    height: Float
): List<Pair<Int, Offset>> {
    val range = (axis.maxValue - axis.minValue).takeIf { it > 0.0 } ?: 1.0
    return points.mapIndexedNotNull { index, point ->
        val value = point.value ?: return@mapIndexedNotNull null
        val stepX = if (points.size <= 1) width / 2f else width * index / (points.size - 1)
        val ratio = ((value - axis.minValue) / range).toFloat()
        val y = height - (ratio * (height * 0.82f)) - 12f
        index to Offset(stepX, y)
    }
}
