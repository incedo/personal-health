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
            val geometry = phAvatarGeometry(
                width = this.size.width,
                height = this.size.height,
                variant = variant,
                selected = selected
            )
            val color = palette.line
            drawCircle(
                color = color,
                radius = geometry.head.radius,
                center = geometry.head.center,
                style = Stroke(width = geometry.stroke)
            )
            drawArc(
                color = color,
                startAngle = geometry.shoulders.startAngle,
                sweepAngle = geometry.shoulders.sweepAngle,
                useCenter = false,
                topLeft = geometry.shoulders.topLeft,
                size = geometry.shoulders.size,
                style = Stroke(width = geometry.stroke, cap = StrokeCap.Round)
            )
            when (val accent = geometry.accent) {
                is PhAvatarAccent.Line -> drawLine(
                    color,
                    accent.start,
                    accent.end,
                    strokeWidth = geometry.stroke,
                    cap = StrokeCap.Round
                )

                is PhAvatarAccent.Arc -> drawArc(
                    color = color,
                    startAngle = accent.startAngle,
                    sweepAngle = accent.sweepAngle,
                    useCenter = false,
                    topLeft = accent.topLeft,
                    size = accent.size,
                    style = Stroke(width = geometry.stroke, cap = StrokeCap.Round)
                )

                is PhAvatarAccent.Dot -> drawCircle(
                    color = color,
                    radius = accent.radius,
                    center = accent.center
                )
            }
            geometry.selection?.let { selection ->
                drawCircle(
                    color = palette.highlight,
                    radius = selection.radius,
                    style = Stroke(width = selection.stroke)
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

internal data class PhAvatarGeometry(
    val stroke: Float,
    val head: PhAvatarCircle,
    val shoulders: PhAvatarArc,
    val accent: PhAvatarAccent,
    val selection: PhAvatarSelection?
)

internal data class PhAvatarCircle(
    val center: Offset,
    val radius: Float
)

internal data class PhAvatarArc(
    val topLeft: Offset,
    val size: Size,
    val startAngle: Float,
    val sweepAngle: Float
)

internal sealed interface PhAvatarAccent {
    data class Line(val start: Offset, val end: Offset) : PhAvatarAccent
    data class Arc(val topLeft: Offset, val size: Size, val startAngle: Float, val sweepAngle: Float) : PhAvatarAccent
    data class Dot(val center: Offset, val radius: Float) : PhAvatarAccent
}

internal data class PhAvatarSelection(
    val radius: Float,
    val stroke: Float
)

internal fun phAvatarGeometry(
    width: Float,
    height: Float,
    variant: PhAvatarVariant,
    selected: Boolean
): PhAvatarGeometry {
    val minDimension = minOf(width, height)
    val stroke = minDimension * 0.07f
    return PhAvatarGeometry(
        stroke = stroke,
        head = PhAvatarCircle(
            center = Offset(width * 0.5f, height * 0.34f),
            radius = minDimension * 0.15f
        ),
        shoulders = PhAvatarArc(
            topLeft = Offset(width * 0.22f, height * 0.46f),
            size = Size(width * 0.56f, height * 0.36f),
            startAngle = 205f,
            sweepAngle = 130f
        ),
        accent = phAvatarAccent(width, height, minDimension, variant),
        selection = if (selected) {
            PhAvatarSelection(radius = minDimension * 0.44f, stroke = stroke * 0.75f)
        } else {
            null
        }
    )
}

private fun phAvatarAccent(
    width: Float,
    height: Float,
    minDimension: Float,
    variant: PhAvatarVariant
): PhAvatarAccent = when (variant) {
    PhAvatarVariant.Masculine -> PhAvatarAccent.Line(
        start = Offset(width * 0.36f, height * 0.24f),
        end = Offset(width * 0.64f, height * 0.24f)
    )

    PhAvatarVariant.Feminine -> PhAvatarAccent.Arc(
        topLeft = Offset(width * 0.28f, height * 0.17f),
        size = Size(width * 0.44f, height * 0.28f),
        startAngle = 205f,
        sweepAngle = 130f
    )

    PhAvatarVariant.Neutral -> PhAvatarAccent.Dot(
        center = Offset(width * 0.5f, height * 0.24f),
        radius = minDimension * 0.04f
    )
}
