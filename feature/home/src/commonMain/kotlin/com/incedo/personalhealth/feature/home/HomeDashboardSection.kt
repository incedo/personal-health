package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun DashboardContent(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    profileName: String,
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    onOpenStepsDetail: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val expanded = maxWidth >= 960.dp
        val spacing = if (compact) 14.dp else 18.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            HomeHeroCard(
                eyebrow = "Today",
                title = "Welkom terug, $profileName",
                subtitle = "Je dagstart in een duidelijk overzicht met herstel, activiteit en de eerstvolgende actie.",
                accent = palette.accent,
                compact = compact,
                sideContent = {
                    ProfileRing(
                        fitScore = fitScore,
                        profileName = profileName,
                        modifier = Modifier.size(if (compact) 176.dp else 208.dp)
                    )
                }
            )

            if (expanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        SummaryStrip(
                            steps = steps,
                            heartRateBpm = heartRateBpm,
                            fitScore = fitScore,
                            onStepsClick = onOpenStepsDetail
                        )
                        StepsOverviewCard(
                            steps = steps,
                            stepsTimeline = stepsTimeline,
                            onOpenDetails = onOpenStepsDetail
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        QuickActivityLogCard(
                            activityOptions = activityOptions,
                            activityEntries = activityEntries,
                            onLogActivity = onLogActivity,
                            modifier = Modifier.fillMaxWidth()
                        )
                        GuidanceCard(
                            fitScore = fitScore,
                            heartRateBpm = heartRateBpm
                        )
                    }
                }
            } else {
                SummaryStrip(
                    steps = steps,
                    heartRateBpm = heartRateBpm,
                    fitScore = fitScore,
                    onStepsClick = onOpenStepsDetail
                )
                StepsOverviewCard(
                    steps = steps,
                    stepsTimeline = stepsTimeline,
                    onOpenDetails = onOpenStepsDetail
                )
                QuickActivityLogCard(
                    activityOptions = activityOptions,
                    activityEntries = activityEntries,
                    onLogActivity = onLogActivity,
                    modifier = Modifier.fillMaxWidth()
                )
                GuidanceCard(
                    fitScore = fitScore,
                    heartRateBpm = heartRateBpm
                )
            }
        }
    }
}

@Composable
private fun SummaryStrip(
    steps: Int,
    heartRateBpm: Int,
    fitScore: Int,
    onStepsClick: () -> Unit
) {
    val palette = homePalette()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryMetricTile(
            title = "Stappen",
            value = formatSteps(steps),
            subtitle = "Vandaag • tik voor details",
            accent = palette.accent,
            onClick = onStepsClick,
            modifier = Modifier.weight(1f)
        )
        SummaryMetricTile(
            title = "Hartslag",
            value = "$heartRateBpm bpm",
            subtitle = "Rustgemiddelde",
            accent = palette.warning,
            modifier = Modifier.weight(1f)
        )
        SummaryMetricTile(
            title = "Score",
            value = "$fitScore/100",
            subtitle = "Dagstatus",
            accent = palette.warm,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
        }
    }
}

@Composable
private fun StepsOverviewCard(
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    onOpenDetails: () -> Unit
) {
    val palette = homePalette()
    HomePanel(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onOpenDetails)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Stappenoverzicht",
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Doel: 10.000 stappen. Tik voor het uur-tot-uur detail van vandaag.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
            HomeStatusBadge(
                label = "Totaal",
                value = formatSteps(steps)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        StepsTodayGraphCard(
            title = "Stappen vandaag",
            subtitle = "Preview",
            stepsTimeline = stepsTimeline,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GuidanceCard(
    fitScore: Int,
    heartRateBpm: Int
) {
    val palette = homePalette()
    val guidance = when {
        fitScore >= 80 -> "Sterke dag. Houd je ritme vast en plan alleen lichte extra belasting."
        heartRateBpm >= 75 -> "Hartslag ligt hoger dan ideaal. Kies vandaag voor herstel of een korte sessie."
        else -> "Je basis is stabiel. Goed moment voor een geplande training of een stevige wandeling."
    }

    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Focus voor vandaag",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = guidance,
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
    }
}

@Composable
private fun QuickActivityLogCard(
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    var selectedType by rememberSaveable { mutableStateOf(activityOptions.firstOrNull() ?: QuickActivityType.RUNNING) }

    HomePanel(modifier = modifier) {
        Text(
            text = "Snelle activiteit",
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
        activityOptions.chunked(3).forEach { activityRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                activityRow.forEach { option ->
                    val selected = option == selectedType
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { selectedType = option },
                        color = if (selected) palette.accentSoft else palette.surface,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) palette.accent else palette.surfaceMuted
                        )
                    ) {
                        Text(
                            text = option.label,
                            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(
            onClick = { onLogActivity(selectedType) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.buttonContent
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Log ${selectedType.label.lowercase()}",
                modifier = Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
        if (activityEntries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            activityEntries.take(4).forEachIndexed { index, entry ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (index == 0) palette.surfaceRaised else palette.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
                ) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.textPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                    )
                }
                if (index < activityEntries.take(4).lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
