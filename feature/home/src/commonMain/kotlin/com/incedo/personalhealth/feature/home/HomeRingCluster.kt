package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

internal data class HomeRingCenterState(
    val heartProgress: Float,
    val scoreText: String? = null,
    val fillHeartFromBottom: Boolean = true,
    val heartSizeFraction: Float = 0.62f,
    val scoreTextStyle: TextStyle
)

@Composable
internal fun HomeRingCluster(
    progressValues: List<Float>,
    colors: List<Color>,
    trackColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    centerSizeFraction: Float = 0.54f,
    strokeWidthPx: Float = 18f,
    ringGapPx: Float = 24f,
    centerState: HomeRingCenterState
) {
    require(progressValues.size == 3) { "HomeRingCluster expects 3 progress values." }
    require(colors.size == 3) { "HomeRingCluster expects 3 ring colors." }
    require(trackColors.size == 3) { "HomeRingCluster expects 3 track colors." }

    BoxWithConstraints(
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        contentAlignment = Alignment.Center
    ) {
        val minSize = minOf(maxWidth, maxHeight)
        val requestedCenterSize = (minSize * centerSizeFraction).coerceAtLeast(64.dp)
        val minVisibleInnerRing = 8.dp
        val density = androidx.compose.ui.platform.LocalDensity.current
        val ringGap = with(density) { ringGapPx.toDp() }
        val strokeWidth = with(density) { strokeWidthPx.toDp() }
        val innerRingDiameter = minSize - (strokeWidth + ringGap) * 4f
        val maxCenterSize = (innerRingDiameter + strokeWidth - minVisibleInnerRing * 2f).coerceAtLeast(64.dp)
        val centerSize = minOf(requestedCenterSize, maxCenterSize)

        Canvas(modifier = Modifier.fillMaxSize()) {
            progressValues.forEachIndexed { index, progress ->
                drawHomeRing(
                    progress = progress.coerceIn(0f, 1f),
                    color = colors[index],
                    trackColor = trackColors[index],
                    strokeWidth = strokeWidthPx,
                    inset = strokeWidthPx / 2f + index * (strokeWidthPx + ringGapPx)
                )
            }
        }

        Box(
            modifier = Modifier.size(centerSize),
            contentAlignment = Alignment.Center
        ) {
            HomeRingCenter(
                state = centerState,
                centerSize = centerSize
            )
        }
    }
}

@Composable
private fun HomeRingCenter(
    state: HomeRingCenterState,
    centerSize: androidx.compose.ui.unit.Dp
) {
    val heartSize = (centerSize * state.heartSizeFraction).coerceAtLeast(40.dp)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            HomeRingHeart(
                progress = state.heartProgress,
                modifier = Modifier.size(heartSize),
                fillFromBottom = state.fillHeartFromBottom
            )
            state.scoreText?.let { scoreText ->
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.34f), shape = RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = scoreText,
                        style = state.scoreTextStyle,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeRingHeart(
    progress: Float,
    modifier: Modifier = Modifier,
    fillFromBottom: Boolean = true
) {
    val palette = homePalette()
    val clampedProgress = progress.coerceIn(0f, 1f)
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
            if (fillFromBottom) {
                val fillHeight = size.height * clampedProgress
                drawRect(
                    color = heartFill,
                    topLeft = Offset(x = 0f, y = size.height - fillHeight),
                    size = Size(width = size.width, height = fillHeight)
                )
            } else {
                drawRect(
                    color = heartFill,
                    topLeft = Offset.Zero,
                    size = Size(width = size.width, height = size.height)
                )
            }
        }
        drawPath(
            path = heartPath,
            color = heartStroke,
            style = Stroke(width = size.minDimension * 0.06f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHomeRing(
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
        style = Stroke(width = strokeWidth)
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
