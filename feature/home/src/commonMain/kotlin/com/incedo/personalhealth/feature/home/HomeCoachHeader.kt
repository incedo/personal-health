package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun CoachHeaderCard(
    compact: Boolean,
    title: String,
    focusProgress: Float,
    goalsProgress: Float,
    coachProgress: Float
) {
    val palette = homePalette()
    val padding = if (compact) 20.dp else 28.dp
    val ringSize = if (compact) 116.dp else 144.dp

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(palette.warning.copy(alpha = 0.2f), palette.surface)
                    )
                )
                .padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            CoachHeaderRings(
                focusProgress = focusProgress,
                goalsProgress = goalsProgress,
                coachProgress = coachProgress,
                modifier = Modifier.size(ringSize)
            )
        }
    }
}

@Composable
private fun CoachHeaderRings(
    focusProgress: Float,
    goalsProgress: Float,
    coachProgress: Float,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val outerStroke = 18f
    val ringGap = 24f

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRing(
                progress = focusProgress.coerceIn(0f, 1f),
                color = palette.accent,
                trackColor = palette.accentSoft.copy(alpha = 0.75f),
                strokeWidth = outerStroke,
                inset = 0f
            )
            drawRing(
                progress = goalsProgress.coerceIn(0f, 1f),
                color = palette.warm,
                trackColor = palette.warmSoft.copy(alpha = 0.75f),
                strokeWidth = outerStroke,
                inset = ringGap
            )
            drawRing(
                progress = coachProgress.coerceIn(0f, 1f),
                color = palette.warning,
                trackColor = palette.warningSoft.copy(alpha = 0.75f),
                strokeWidth = outerStroke,
                inset = ringGap * 2
            )
        }
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(color = palette.surface, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CoachHeaderHeart(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun CoachHeaderHeart(
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val heartTrack = palette.warningSoft.copy(alpha = 0.95f)
    val heartFill = palette.warning
    val heartStroke = palette.warning.copy(alpha = 0.9f)

    Canvas(modifier = modifier) {
        val heartPath = Path().apply {
            val width = size.width
            val height = size.height

            moveTo(width * 0.5f, height * 0.92f)
            cubicTo(width * 0.1f, height * 0.68f, width * 0.02f, height * 0.34f, width * 0.28f, height * 0.2f)
            cubicTo(width * 0.43f, height * 0.11f, width * 0.5f, height * 0.19f, width * 0.5f, height * 0.28f)
            cubicTo(width * 0.5f, height * 0.19f, width * 0.57f, height * 0.11f, width * 0.72f, height * 0.2f)
            cubicTo(width * 0.98f, height * 0.34f, width * 0.9f, height * 0.68f, width * 0.5f, height * 0.92f)
            close()
        }

        drawPath(path = heartPath, color = heartTrack)
        clipPath(heartPath) {
            drawRect(
                color = heartFill,
                topLeft = Offset.Zero,
                size = Size(width = size.width, height = size.height)
            )
        }
        drawPath(
            path = heartPath,
            color = heartStroke,
            style = Stroke(width = size.minDimension * 0.06f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRing(
    progress: Float,
    color: Color,
    trackColor: Color,
    strokeWidth: Float,
    inset: Float
) {
    drawArc(
        color = trackColor,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(inset, inset),
        size = Size(width = size.width - inset * 2, height = size.height - inset * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
    drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle = 360f * progress,
        useCenter = false,
        topLeft = Offset(inset, inset),
        size = Size(width = size.width - inset * 2, height = size.height - inset * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}
