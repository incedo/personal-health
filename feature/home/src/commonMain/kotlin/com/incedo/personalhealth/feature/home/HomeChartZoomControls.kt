package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

internal const val MinChartZoomLevel = 1f
internal const val MaxChartZoomLevel = 2.4f
private const val ChartZoomStep = 0.2f

internal fun nextChartZoomLevel(
    current: Float,
    zoomIn: Boolean
): Float {
    val delta = if (zoomIn) ChartZoomStep else -ChartZoomStep
    return (current + delta).coerceIn(MinChartZoomLevel, MaxChartZoomLevel)
}

@Composable
internal fun HomeChartZoomControls(
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ChartZoomButton(label = "uit", zoomIn = false, onClick = onZoomOut)
        ChartZoomButton(label = "in", zoomIn = true, onClick = onZoomIn)
    }
}

@Composable
private fun ChartZoomButton(
    label: String,
    zoomIn: Boolean,
    onClick: () -> Unit
) {
    val palette = homePalette()
    Surface(
        modifier = Modifier
            .widthIn(min = 88.dp)
            .clickable(onClick = onClick),
        color = palette.surface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .widthIn(min = 88.dp)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZoomGlyph(
                zoomIn = zoomIn,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ZoomGlyph(
    zoomIn: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.12f
        drawCircle(
            color = palette.textPrimary,
            radius = size.minDimension * 0.28f,
            center = Offset(size.width * 0.42f, size.height * 0.42f),
            style = Stroke(width = stroke)
        )
        drawLine(
            color = palette.textPrimary,
            start = Offset(size.width * 0.62f, size.height * 0.62f),
            end = Offset(size.width * 0.86f, size.height * 0.86f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = palette.textPrimary,
            start = Offset(size.width * 0.28f, size.height * 0.42f),
            end = Offset(size.width * 0.56f, size.height * 0.42f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        if (zoomIn) {
            drawLine(
                color = palette.textPrimary,
                start = Offset(size.width * 0.42f, size.height * 0.28f),
                end = Offset(size.width * 0.42f, size.height * 0.56f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}
