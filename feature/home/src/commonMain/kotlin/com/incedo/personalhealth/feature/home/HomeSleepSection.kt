package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun SleepDetailScreen(
    metric: HomeHealthMetricCard,
    onBack: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    val spacing = if (compact) 14.dp else 18.dp

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        HomeHeroCard(
            eyebrow = "Slaap",
            title = "Slaapdetails",
            subtitle = "Een losse detailpagina voor slaap, zodat hier later acties en extra inzichten bij kunnen komen.",
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

        SleepInsightCard(
            title = "Laatste slaapsessie",
            body = metric.detail,
            modifier = Modifier.fillMaxWidth()
        )
        SleepInsightCard(
            title = "Databron",
            body = metric.sourceSummary,
            modifier = Modifier.fillMaxWidth()
        )
        SleepInsightCard(
            title = "Volgende stap",
            body = "Hier kunnen we straks acties, kwaliteitscores, trends en broninformatie voor slaap toevoegen zonder het overzichtsscherm te belasten.",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SleepInsightCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Surface(
        modifier = modifier,
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }
    }
}
