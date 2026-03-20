package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun HealthDataDetailScreen(
    metrics: List<HomeHealthMetricCard>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onOpenSleepDetail: () -> Unit,
    onOpenWeightDetail: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    val spacing = if (compact) 14.dp else 18.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        HomeHeroCard(
            eyebrow = "Health Connect",
            title = "Alle health data",
            subtitle = "Overzicht van de data die de app nu uit Health Connect kan lezen en tonen.",
            accent = palette.accent,
            compact = compact,
            sideContent = {
                HomeStatusBadge(
                    label = "Metrics",
                    value = metrics.size.toString()
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.surfaceRaised,
                    contentColor = palette.textPrimary
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Terug")
            }
            Button(
                onClick = onRefresh,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.warm,
                    contentColor = palette.textPrimary
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Vernieuw")
            }
        }

        if (metrics.isEmpty()) {
            HomePanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nog geen Health Connect data zichtbaar.",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gebruik profiel/import om permissies te beheren en ververs daarna om stappen, hartslag, slaap, actieve energie en gewicht hier op te lijsten.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
        } else {
            metrics.forEach { metric ->
                HealthMetricCard(
                    metric = metric,
                    onClick = when (metric.id) {
                        SLEEP_HEALTH_METRIC_ID -> onOpenSleepDetail
                        BODY_WEIGHT_HEALTH_METRIC_ID -> onOpenWeightDetail
                        else -> null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HealthMetricCard(
    metric: HomeHealthMetricCard,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val accent = when (metric.accent) {
        HomeInsightTone.ACCENT -> palette.accent
        HomeInsightTone.WARM -> palette.warm
        HomeInsightTone.WARNING -> palette.warning
    }

    HomePanel(modifier = if (onClick == null) modifier else modifier.clip(RoundedCornerShape(28.dp)).clickable(onClick = onClick)) {
        Text(
            text = metric.title,
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = metric.value,
            style = MaterialTheme.typography.headlineSmall,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = metric.detail,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        if (onClick != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tik voor details",
                style = MaterialTheme.typography.labelMedium,
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(999.dp),
            color = palette.surfaceMuted
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(metric.progress.coerceIn(0f, 1f))
                        .height(12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(accent)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(accent)
            )
            Text(
                text = "${metric.id} • ${metric.sourceSummary}",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary
            )
        }
    }
}
