package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun NewsSocialSection() {
    val palette = homePalette()
    val highlights = listOf(
        Triple("Community run", "Zaterdag 08:30 • Vondelpark", "24 mensen gaan"),
        Triple("Herstel-tip", "Korte mobility-flow van 8 minuten", "Past goed na krachttraining"),
        Triple("Voedingstrend", "Meer eiwit bij ontbijt blijft populair", "Bekijk wat vandaag werkt")
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        highlights.forEachIndexed { index, item ->
            val accent = when (index) {
                0 -> palette.accent
                1 -> palette.warning
                else -> palette.warm
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = palette.surfaceRaised,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.first,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = item.second,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary
                        )
                        Text(
                            text = item.third,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        NewsTrainingVideoStrip()
    }
}
