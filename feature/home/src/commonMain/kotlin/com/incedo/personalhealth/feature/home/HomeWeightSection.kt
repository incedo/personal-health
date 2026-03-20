package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun WeightDetailScreen(
    metric: HomeHealthMetricCard,
    catalog: HomeWeightChartCatalog,
    onBack: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    var selectedRange by remember { mutableStateOf(HomeWeightRange.MONTH) }
    var zoomLevel by remember { mutableFloatStateOf(1.35f) }
    val timeline = catalog.timelineFor(selectedRange)
    val stats = weightDetailStats(timeline.points)
    val spacing = if (compact) 14.dp else 18.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        HomeHeroCard(
            eyebrow = "Gewicht",
            title = "Gewicht trends",
            subtitle = "Bekijk gewicht op week-, maand-, kwartaal-, jaar- en all-time niveau met dezelfde detailpagina.",
            accent = palette.accent,
            compact = compact,
            sideContent = {
                Text(
                    text = metric.value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        )

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.surfaceRaised,
                contentColor = palette.textPrimary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Terug")
        }

        WeightSummaryCard(
            stats = stats,
            sourceSummary = metric.sourceSummary,
            modifier = Modifier.fillMaxWidth()
        )
        WeightTimelineCard(
            timeline = timeline,
            selectedRange = selectedRange,
            zoomLevel = zoomLevel,
            onRangeSelected = { selectedRange = it },
            onZoomIn = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(3.2f) },
            onZoomOut = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.8f) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun WeightSummaryCard(
    stats: WeightDetailStats,
    sourceSummary: String,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    HomePanel(modifier = modifier) {
        Text(
            text = "Meetoverzicht",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text("Laatste meting: ${formatWeightKg(stats.latestWeightKg)}", color = palette.textSecondary)
        Text("Bandbreedte: ${formatWeightKg(stats.minWeightKg)} tot ${formatWeightKg(stats.maxWeightKg)}", color = palette.textSecondary)
        Text("Meetdagen: ${stats.measuredDays} van 14", color = palette.textSecondary)
        Text(
            "Verandering: ${stats.changeKg?.let(::formatWeightDelta) ?: "Nog niet genoeg data"}",
            color = palette.textSecondary
        )
        Text("Bron: $sourceSummary", color = palette.textSecondary)
    }
}

private fun formatWeightDelta(deltaKg: Double): String = if (deltaKg >= 0) {
    "+${formatWeightKg(deltaKg)}"
} else {
    formatWeightKg(deltaKg)
}
