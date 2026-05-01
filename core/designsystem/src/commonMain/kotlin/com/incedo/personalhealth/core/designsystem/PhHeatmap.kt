package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PhHeatmap(
    data: List<Float>,
    modifier: Modifier = Modifier,
    rows: Int = 7,
    columns: Int = 12,
    max: Float = 1f,
    cellSize: Dp = 14.dp,
    gap: Dp = 4.dp,
    emptyColor: Color = PhTheme.colors.surfaceMuted,
    activeColor: Color = PhTheme.colors.primary
) {
    val width = cellSize * columns + gap * (columns - 1)
    val height = cellSize * rows + gap * (rows - 1)
    Canvas(modifier = modifier.width(width).height(height)) {
        val cellPx = cellSize.toPx()
        val gapPx = gap.toPx()
        repeat(rows) { row ->
            repeat(columns) { column ->
                val index = row * columns + column
                val value = data.getOrNull(index)?.coerceIn(0f, max) ?: 0f
                drawRoundRect(
                    color = if (value == 0f) emptyColor else activeColor.copy(alpha = 0.2f + 0.8f * (value / max)),
                    topLeft = Offset(column * (cellPx + gapPx), row * (cellPx + gapPx)),
                    size = Size(cellPx, cellPx),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
            }
        }
    }
}
