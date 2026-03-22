package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun HomeTabIcon(
    tab: HomeTab,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.09f
        when (tab) {
            HomeTab.DASHBOARD -> {
                drawLine(color, Offset(size.width * 0.18f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.18f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.82f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.18f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.24f, size.height * 0.46f), Offset(size.width * 0.24f, size.height * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.76f, size.height * 0.46f), Offset(size.width * 0.76f, size.height * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.24f, size.height * 0.8f), Offset(size.width * 0.76f, size.height * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
            }
            HomeTab.NEWS -> {
                drawCircle(color = color, radius = size.minDimension * 0.28f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = Stroke(width = stroke))
                drawArc(color = color, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(size.width * 0.18f, size.height * 0.32f), size = Size(size.width * 0.64f, size.height * 0.28f), style = Stroke(width = stroke, cap = StrokeCap.Round))
                drawArc(color = color, startAngle = 20f, sweepAngle = 140f, useCenter = false, topLeft = Offset(size.width * 0.18f, size.height * 0.4f), size = Size(size.width * 0.64f, size.height * 0.28f), style = Stroke(width = stroke, cap = StrokeCap.Round))
                drawLine(color, Offset(size.width * 0.22f, size.height * 0.5f), Offset(size.width * 0.78f, size.height * 0.5f), strokeWidth = stroke, cap = StrokeCap.Round)
            }
            HomeTab.COACH -> {
                drawCircle(color = color, radius = size.minDimension * 0.14f, center = Offset(size.width * 0.5f, size.height * 0.26f), style = Stroke(width = stroke))
                drawArc(color = color, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(size.width * 0.18f, size.height * 0.34f), size = Size(size.width * 0.64f, size.height * 0.42f), style = Stroke(width = stroke, cap = StrokeCap.Round))
                drawLine(color, Offset(size.width * 0.5f, size.height * 0.46f), Offset(size.width * 0.5f, size.height * 0.68f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.38f, size.height * 0.58f), Offset(size.width * 0.62f, size.height * 0.58f), strokeWidth = stroke, cap = StrokeCap.Round)
            }
            HomeTab.LOG -> {
                drawLine(color, Offset(size.width * 0.3f, size.height * 0.2f), Offset(size.width * 0.7f, size.height * 0.2f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.3f, size.height * 0.2f), Offset(size.width * 0.3f, size.height * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.7f, size.height * 0.2f), Offset(size.width * 0.7f, size.height * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.3f, size.height * 0.8f), Offset(size.width * 0.7f, size.height * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.38f, size.height * 0.38f), Offset(size.width * 0.62f, size.height * 0.38f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.38f, size.height * 0.52f), Offset(size.width * 0.62f, size.height * 0.52f), strokeWidth = stroke, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.38f, size.height * 0.66f), Offset(size.width * 0.54f, size.height * 0.66f), strokeWidth = stroke, cap = StrokeCap.Round)
            }
            HomeTab.PROFILE -> {
                drawCircle(color = color, radius = size.minDimension * 0.16f, center = Offset(size.width * 0.5f, size.height * 0.3f), style = Stroke(width = stroke))
                drawArc(color = color, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(size.width * 0.2f, size.height * 0.38f), size = Size(size.width * 0.6f, size.height * 0.44f), style = Stroke(width = stroke, cap = StrokeCap.Round))
            }
        }
    }
}
