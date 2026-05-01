package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PhRingGauge(
    value: Float,
    modifier: Modifier = Modifier,
    max: Float = 100f,
    size: Dp = 160.dp,
    strokeWidth: Dp = 14.dp,
    label: String? = null,
    sublabel: String? = null,
    color: Color = PhTheme.colors.primary,
    trackColor: Color = PhTheme.colors.surfaceMuted
) {
    val progress = (value / max).coerceIn(0f, 1f)
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val diameter = this.size.minDimension - stroke
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            val arcSize = Size(diameter, diameter)
            drawArc(trackColor, -90f, 360f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            drawArc(color, -90f, progress * 360f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value.toInt().toString(), style = PhTheme.typography.metric, color = PhTheme.colors.text)
            label?.let { Text(text = it, style = PhTheme.typography.label, color = PhTheme.colors.textMuted) }
            sublabel?.let { Text(text = it, style = PhTheme.typography.caption, color = PhTheme.colors.textFaint) }
        }
    }
}

@Composable
fun PhTripleRing(
    rings: List<Float>,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 12.dp,
    gap: Dp = 6.dp,
    label: String? = null,
    sublabel: String? = null
) {
    val colors = PhTheme.colors.data
    val trackColor = PhTheme.colors.surfaceMuted.copy(alpha = 0.65f)
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val ringGap = gap.toPx()
            rings.take(3).forEachIndexed { index, ring ->
                val inset = index * (stroke + ringGap) + stroke / 2f
                val diameter = this.size.minDimension - inset * 2f
                val arcSize = Size(diameter, diameter)
                val topLeft = Offset(inset, inset)
                drawArc(trackColor, -90f, 360f, false, topLeft, arcSize, style = Stroke(stroke))
                drawArc(
                    colors[index],
                    -90f,
                    ring.coerceIn(0f, 1f) * 360f,
                    false,
                    topLeft,
                    arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            label?.let { Text(text = it, style = PhTheme.typography.h3, color = PhTheme.colors.text) }
            sublabel?.let { Text(text = it, style = PhTheme.typography.caption, color = PhTheme.colors.textMuted) }
        }
    }
}

@Composable
fun PhSparkline(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = PhTheme.colors.primary,
    fillColor: Color = PhTheme.colors.primarySoft,
    height: Dp = 64.dp,
    showDots: Boolean = false
) {
    Canvas(modifier = modifier.height(height)) {
        if (data.isEmpty()) return@Canvas
        val min = data.minOrNull() ?: 0f
        val max = data.maxOrNull() ?: 1f
        val range = (max - min).takeUnless { it == 0f } ?: 1f
        val points = data.mapIndexed { index, value ->
            val x = if (data.size == 1) size.width else index * size.width / (data.lastIndex)
            val y = size.height - ((value - min) / range) * size.height
            Offset(x, y)
        }
        val line = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val fill = Path().apply {
            addPath(line)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(fill, fillColor.copy(alpha = 0.55f))
        drawPath(line, lineColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        if (showDots) points.forEach { drawCircle(lineColor, 3.dp.toPx(), it) }
    }
}

@Composable
fun PhBars(
    data: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = PhTheme.colors.primary,
    mutedColor: Color = PhTheme.colors.accent,
    height: Dp = 100.dp
) {
    Canvas(modifier = modifier.height(height)) {
        if (data.isEmpty()) return@Canvas
        val max = (data.maxOrNull() ?: 1f).coerceAtLeast(1f)
        val gap = 6.dp.toPx()
        val barWidth = (size.width - gap * (data.size - 1)) / data.size
        data.forEachIndexed { index, value ->
            val barHeight = size.height * (value / max).coerceIn(0f, 1f)
            drawRoundRect(
                color = if (index == data.lastIndex) color else mutedColor.copy(alpha = 0.7f),
                topLeft = Offset(index * (barWidth + gap), size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}

@Composable
fun PhZoneBar(
    zones: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp
) {
    val zoneColors = PhTheme.colors.zones
    Row(modifier = modifier.height(height)) {
        val total = zones.sum().takeUnless { it == 0f } ?: 1f
        zones.forEachIndexed { index, value ->
            Box(
                modifier = Modifier
                    .weight(value / total)
                    .height(height)
                    .then(Modifier)
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    drawRect(zoneColors[index.coerceAtMost(zoneColors.lastIndex)])
                }
            }
        }
    }
}

internal fun pointOnCircle(center: Offset, radius: Float, degrees: Float): Offset {
    val radians = degrees * PI.toFloat() / 180f
    return Offset(center.x + radius * cos(radians), center.y + radius * sin(radians))
}
