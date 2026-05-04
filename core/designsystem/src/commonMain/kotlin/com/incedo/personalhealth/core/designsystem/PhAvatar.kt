package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PhAvatarVariant {
    Masculine,
    Feminine,
    Neutral
}

@Composable
fun PhAvatar(
    variant: PhAvatarVariant,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    selected: Boolean = false
) {
    val palette = phAvatarPalette(variant)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(palette.backgroundStart, palette.backgroundEnd))),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.74f)) {
            val stroke = this.size.minDimension * 0.07f
            val color = palette.line
            drawCircle(
                color = color,
                radius = this.size.minDimension * 0.15f,
                center = Offset(this.size.width * 0.5f, this.size.height * 0.34f),
                style = Stroke(width = stroke)
            )
            drawArc(
                color = color,
                startAngle = 205f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(this.size.width * 0.22f, this.size.height * 0.46f),
                size = Size(this.size.width * 0.56f, this.size.height * 0.36f),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            when (variant) {
                PhAvatarVariant.Masculine -> drawLine(
                    color,
                    Offset(this.size.width * 0.36f, this.size.height * 0.24f),
                    Offset(this.size.width * 0.64f, this.size.height * 0.24f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                PhAvatarVariant.Feminine -> drawArc(
                    color = color,
                    startAngle = 205f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(this.size.width * 0.28f, this.size.height * 0.17f),
                    size = Size(this.size.width * 0.44f, this.size.height * 0.28f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )

                PhAvatarVariant.Neutral -> drawCircle(
                    color = color,
                    radius = this.size.minDimension * 0.04f,
                    center = Offset(this.size.width * 0.5f, this.size.height * 0.24f)
                )
            }
            if (selected) {
                drawCircle(
                    color = palette.highlight,
                    radius = this.size.minDimension * 0.44f,
                    style = Stroke(width = stroke * 0.75f)
                )
            }
        }
    }
}

private data class PhAvatarPalette(
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val line: Color,
    val highlight: Color
)

@Composable
private fun phAvatarPalette(variant: PhAvatarVariant): PhAvatarPalette {
    val colors = PhTheme.colors
    return when (variant) {
        PhAvatarVariant.Masculine -> PhAvatarPalette(colors.primarySoft, colors.infoSoft, colors.primary, colors.primary)
        PhAvatarVariant.Feminine -> PhAvatarPalette(colors.warningSoft, colors.dangerSoft, colors.danger, colors.warning)
        PhAvatarVariant.Neutral -> PhAvatarPalette(colors.surfaceMuted, colors.primarySoft, colors.textMuted, colors.primary)
    }
}
