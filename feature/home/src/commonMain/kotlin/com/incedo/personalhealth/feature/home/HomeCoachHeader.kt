package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun CoachHeaderCard(
    compact: Boolean,
    title: String,
    fitScore: Int,
    steps: Int,
    heartRateBpm: Int
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
                fitScore = fitScore,
                steps = steps,
                heartRateBpm = heartRateBpm,
                centerSizeFraction = (72f / ringSize.value).coerceIn(0.5f, 0.64f),
                modifier = Modifier.size(ringSize)
            )
        }
    }
}

@Composable
private fun CoachHeaderRings(
    fitScore: Int,
    steps: Int,
    heartRateBpm: Int,
    centerSizeFraction: Float,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val vitalityProgress = fitScore.coerceIn(0, 100) / 100f
    val movementProgress = (steps / 6000f).coerceIn(0f, 1f)
    val recoveryProgress = ((84 - heartRateBpm).coerceIn(0, 28) / 28f).coerceIn(0f, 1f)

    HomeRingCluster(
        progressValues = listOf(vitalityProgress, movementProgress, recoveryProgress),
        colors = listOf(palette.accent, palette.warm, palette.warning),
        trackColors = listOf(
            palette.accentSoft.copy(alpha = 0.75f),
            palette.warmSoft.copy(alpha = 0.75f),
            palette.warningSoft.copy(alpha = 0.75f)
        ),
        modifier = modifier,
        centerSizeFraction = centerSizeFraction,
        centerState = HomeRingCenterState(
            heartProgress = vitalityProgress,
            scoreText = fitScore.coerceIn(0, 100).toString(),
            heartSizeFraction = 0.62f,
            scoreTextStyle = MaterialTheme.typography.headlineSmall
        )
    )
}
