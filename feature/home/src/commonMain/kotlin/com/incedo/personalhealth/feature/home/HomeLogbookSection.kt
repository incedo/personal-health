package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun LogbookSection(
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityEntries: List<QuickActivityEntry>,
    nutritionEntries: List<NutritionLogEntry>,
    nowEpochMillis: Long,
    onStartActivity: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onAddNutrition: () -> Unit,
    onUpdateNutrition: (NutritionLogEntry) -> Unit
) {
    val palette = homePalette()
    val quickOptions = listOf(
        QuickActivityType.NUTRITION, QuickActivityType.WALKING, QuickActivityType.RUNNING,
        QuickActivityType.CYCLING, QuickActivityType.SWIMMING, QuickActivityType.FITNESS
    ).filter { it in activityOptions }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        HomePanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Snel loggen",
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Start een activiteit en stop hem zodra je klaar bent. De duur wordt automatisch bijgehouden voor je voortgang.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            quickOptions.chunked(3).forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { option ->
                        val activityBlocked = option != QuickActivityType.NUTRITION && activeActivity != null
                        val activeType = activeActivity?.type == option
                        val label = when {
                            option == QuickActivityType.NUTRITION -> "Eten / drinken"
                            activeType -> "${option.label}\nBezig"
                            else -> option.label
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable(enabled = !activityBlocked) {
                                    if (option == QuickActivityType.NUTRITION) {
                                        onAddNutrition()
                                    } else {
                                        onStartActivity(option)
                                    }
                                },
                            color = when {
                                option == QuickActivityType.NUTRITION -> palette.warmSoft
                                activeType -> palette.accentSoft
                                activityBlocked -> palette.surface
                                else -> palette.surfaceRaised
                            },
                            shape = RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = when {
                                    option == QuickActivityType.NUTRITION -> palette.warm
                                    activeType -> palette.accent
                                    else -> palette.surfaceMuted
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QuickActivityIcon(
                                    type = option,
                                    color = if (option == QuickActivityType.NUTRITION) palette.warm else palette.accent,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.textPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if (rowIndex < quickOptions.chunked(3).lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        HomePanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Vandaag gelogd",
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = quickActivitySummary(activityEntries),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (activeActivity != null) {
                ActiveActivityCard(
                    session = activeActivity,
                    nowEpochMillis = nowEpochMillis,
                    onStopActivity = onStopActivity
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (activityEntries.isEmpty() && nutritionEntries.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = palette.surfaceRaised,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
                ) {
                    Text(
                        text = "Nog niets gelogd. Start een activiteit of voeg eten en drinken toe via de knoppen hierboven.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            } else {
                val feedItems = buildLogbookFeedItems(
                    activityEntries = activityEntries,
                    nutritionEntries = nutritionEntries
                ).take(12)
                feedItems.forEachIndexed { index, item ->
                    when (item) {
                        is LogbookFeedItem.Activity -> QuickLogCard(entry = item.entry)
                        is LogbookFeedItem.Nutrition -> NutritionLogCard(
                            entry = item.entry,
                            onSaveEntry = onUpdateNutrition
                        )
                    }
                    if (index < feedItems.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickLogCard(entry: QuickActivityEntry) {
    val palette = homePalette()
    val accent = when (entry.type) {
        QuickActivityType.NUTRITION -> palette.warm
        QuickActivityType.FITNESS -> palette.warning
        else -> palette.accent
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                QuickActivityIcon(
                    type = entry.type,
                    color = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(entry.title, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(listOfNotNull(entry.type.label, entry.durationMillis?.let(::formatActivitySummaryDuration)).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary)
            }
        }
    }
}
