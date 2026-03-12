package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    profileName: String,
    syncContent: @Composable ColumnScope.() -> Unit,
    profileContent: @Composable ColumnScope.() -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.DASHBOARD) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        icon = { Text(tab.shortLabel) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                HomeTab.DASHBOARD -> DashboardContent(
                    fitScore = fitScore,
                    steps = steps,
                    stepsTimeline = stepsTimeline,
                    heartRateBpm = heartRateBpm,
                    profileName = profileName
                )
                HomeTab.SYNC -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = syncContent
                )
                HomeTab.PROFILE -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileFitScoreCard(
                        fitScore = fitScore,
                        profileName = profileName,
                        modifier = Modifier.fillMaxWidth()
                    )
                    profileContent()
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    profileName: String
) {
    var isStepsGraphVisible by rememberSaveable { mutableStateOf(false) }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val expanded = maxWidth >= 840.dp
        if (expanded) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Vandaag", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    MetricCard(
                        title = "Fit score",
                        value = "$fitScore / 100",
                        subtitle = "Dagscore op basis van activiteit en herstel"
                    )
                    MetricCard(
                        title = "Stappen",
                        value = formatSteps(steps),
                        subtitle = "Doel: 10.000 stappen (tik voor grafiek)",
                        onClick = { isStepsGraphVisible = !isStepsGraphVisible }
                    )
                    if (isStepsGraphVisible) {
                        StepsTodayGraphCard(
                            stepsTimeline = stepsTimeline,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    MetricCard(
                        title = "Hartslag",
                        value = "$heartRateBpm bpm",
                        subtitle = "Gemiddelde rusthartslag"
                    )
                }
                ProfileFitScoreCard(
                    fitScore = fitScore,
                    profileName = profileName,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Vandaag", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                ProfileFitScoreCard(
                    fitScore = fitScore,
                    profileName = profileName,
                    modifier = Modifier.fillMaxWidth()
                )
                MetricCard(
                    title = "Fit score",
                    value = "$fitScore / 100",
                    subtitle = "Dagscore op basis van activiteit en herstel"
                )
                MetricCard(
                    title = "Stappen",
                    value = formatSteps(steps),
                    subtitle = "Doel: 10.000 stappen (tik voor grafiek)",
                    onClick = { isStepsGraphVisible = !isStepsGraphVisible }
                )
                if (isStepsGraphVisible) {
                    StepsTodayGraphCard(
                        stepsTimeline = stepsTimeline,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                MetricCard(
                    title = "Hartslag",
                    value = "$heartRateBpm bpm",
                    subtitle = "Gemiddelde rusthartslag"
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StepsTodayGraphCard(
    stepsTimeline: List<StepTimelinePoint>,
    modifier: Modifier = Modifier
) {
    val maxValue = (stepsTimeline.maxOfOrNull { it.steps } ?: 0).coerceAtLeast(1)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Stappen vandaag",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                stepsTimeline.forEach { point ->
                    val ratio = (point.steps.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = point.steps.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((78.dp * ratio).coerceAtLeast(4.dp))
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = point.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(
                text = "Totaal: ${formatSteps(stepsTimeline.sumOf { it.steps })}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileFitScoreCard(
    fitScore: Int,
    profileName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Jouw fitheid vandaag",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            ProfileRing(
                fitScore = fitScore,
                profileName = profileName
            )
        }
    }
}

@Composable
private fun ProfileRing(
    fitScore: Int,
    profileName: String
) {
    val clamped = fitScore.coerceIn(0, 100)
    val progress = clamped / 100f
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary
    val initials = profileName.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifBlank { "PH" }

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 18.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "$clamped%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private enum class HomeTab(
    val label: String,
    val shortLabel: String
) {
    DASHBOARD(label = "Home", shortLabel = "H"),
    SYNC(label = "Sync", shortLabel = "S"),
    PROFILE(label = "Profiel", shortLabel = "P")
}
