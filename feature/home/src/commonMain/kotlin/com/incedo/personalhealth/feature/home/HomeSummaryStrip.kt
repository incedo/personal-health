package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun SummaryStrip(
    steps: Int,
    heartRateBpm: Int,
    weightSummary: String,
    fitScore: Int,
    onStepsClick: () -> Unit,
    onHeartRateClick: () -> Unit,
    onWeightClick: () -> Unit,
    compact: Boolean = true,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val arrangement = if (compact) Arrangement.spacedBy(12.dp) else Arrangement.spacedBy(10.dp)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        SummaryMetricTile(
            title = "Stappen",
            value = formatSteps(steps),
            subtitle = "Vandaag • tik voor details",
            accent = palette.accent,
            onClick = onStepsClick,
            compact = compact,
            modifier = Modifier.weight(1f)
        )
        SummaryMetricTile(
            title = "Hartslag",
            value = "$heartRateBpm bpm",
            subtitle = "Vandaag • tik voor details",
            accent = palette.warning,
            onClick = onHeartRateClick,
            compact = compact,
            modifier = Modifier.weight(1f)
        )
        SummaryMetricTile(
            title = "Gewicht",
            value = weightSummary,
            subtitle = "Laatste meting • tik voor overzicht",
            accent = palette.accent,
            onClick = onWeightClick,
            compact = compact,
            modifier = Modifier.weight(1f)
        )
        SummaryMetricTile(
            title = "Score",
            value = "$fitScore/100",
            subtitle = "Dagstatus",
            accent = palette.warm,
            compact = compact,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryMetricTile(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    onClick: (() -> Unit)? = null,
    compact: Boolean = true,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Card(
        modifier = if (onClick == null) {
            modifier
        } else {
            modifier
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onClick)
        },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = if (compact) 18.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = palette.textSecondary
            )
            Text(
                text = value,
                style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = accent
            )
        }
    }
}
