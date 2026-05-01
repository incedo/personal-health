package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhButton
import com.incedo.personalhealth.core.designsystem.PhButtonVariant
import com.incedo.personalhealth.core.designsystem.PhMetricCard
import com.incedo.personalhealth.core.designsystem.PhSparkline
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.recommendations.DailyRecommendation
import com.incedo.personalhealth.core.recommendations.DailyRecommendationInsight
import com.incedo.personalhealth.core.recommendations.RecommendationInsightTone

@Composable
internal fun DashboardContent(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    weightSummary: String,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    dailyRecommendation: DailyRecommendation,
    profileName: String,
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityClockEpochMillis: Long,
    onStartActivity: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onLogNutrition: () -> Unit,
    onOpenStepsDetail: () -> Unit,
    onOpenHeartRateDetail: () -> Unit,
    onOpenWeightDetail: () -> Unit,
    onOpenHealthDataDetail: () -> Unit,
    compact: Boolean
) {
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
            VitalityLandingCard(
                fitScore = fitScore,
                steps = steps,
                weightSummary = weightSummary,
                activityMinutesToday = activityMinutesToday,
                heartRateBpm = heartRateBpm,
                dailyRecommendation = dailyRecommendation,
                profileName = profileName,
                activityOptions = activityOptions,
                activeActivity = activeActivity,
                activityClockEpochMillis = activityClockEpochMillis,
                compact = compact,
                onStartWorkout = onStartActivity,
                onStopActivity = onStopActivity,
                onOpenWeightDetail = onOpenWeightDetail,
                onOpenHealthDataDetail = onOpenHealthDataDetail,
                onLogNutrition = onLogNutrition
            )
            DashboardMetricsGrid(
                expanded = expanded,
                steps = steps,
                stepsTimeline = stepsTimeline,
                weightSummary = weightSummary,
                activityMinutesToday = activityMinutesToday,
                heartRateBpm = heartRateBpm,
                onOpenStepsDetail = onOpenStepsDetail,
                onOpenHeartRateDetail = onOpenHeartRateDetail,
                onOpenWeightDetail = onOpenWeightDetail
            )
            DashboardPanelGrid(
                expanded = expanded,
                steps = steps,
                stepsTimeline = stepsTimeline,
                weightSummary = weightSummary,
                activityMinutesToday = activityMinutesToday,
                heartRateBpm = heartRateBpm,
                dailyRecommendation = dailyRecommendation,
                activityOptions = activityOptions,
                activeActivity = activeActivity,
                activityClockEpochMillis = activityClockEpochMillis
            )
        }
    }
}

@Composable
private fun DashboardMetricsGrid(
    expanded: Boolean,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    weightSummary: String,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    onOpenStepsDetail: () -> Unit,
    onOpenHeartRateDetail: () -> Unit,
    onOpenWeightDetail: () -> Unit
) {
    val sparklineData = stepsTimeline.map { it.steps.toFloat() }.ifEmpty { listOf(0f, steps.toFloat()) }
    val cards = listOf<@Composable (Modifier) -> Unit>(
        { modifier ->
            PhMetricCard(
                label = "Stappen",
                value = formatSteps(steps),
                trend = "Vandaag",
                tone = PhTagTone.Primary,
                chart = { PhSparkline(data = sparklineData, modifier = Modifier.fillMaxWidth()) },
                modifier = modifier.clickable(onClick = onOpenStepsDetail)
            )
        },
        { modifier ->
            PhMetricCard(
                label = "Hartslag",
                value = heartRateBpm.toString(),
                unit = "bpm",
                trend = "Herstel",
                tone = PhTagTone.Danger,
                modifier = modifier.clickable(onClick = onOpenHeartRateDetail)
            )
        },
        { modifier ->
            PhMetricCard(
                label = "Activiteit",
                value = activityMinutesToday.toString(),
                unit = "min",
                trend = "Training load",
                tone = PhTagTone.Warning,
                modifier = modifier
            )
        },
        { modifier ->
            PhMetricCard(
                label = "Gewicht",
                value = weightSummary,
                trend = "Trend",
                tone = PhTagTone.Info,
                modifier = modifier.clickable(onClick = onOpenWeightDetail)
            )
        }
    )

    if (expanded) {
        var cursor = 0
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            dashboardMetricRowSizes(expanded = true, cardCount = cards.size).forEach { rowSize ->
                val rowCards = cards.drop(cursor).take(rowSize)
                cursor += rowSize
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    rowCards.forEach { card -> card(Modifier.weight(1f)) }
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            cards.forEach { card -> card(Modifier.fillMaxWidth()) }
        }
    }
}

internal fun dashboardMetricRowSizes(expanded: Boolean, cardCount: Int): List<Int> {
    if (cardCount <= 0) return emptyList()
    return if (expanded) {
        List((cardCount + 1) / 2) { index ->
            if (index == cardCount / 2 && cardCount % 2 == 1) 1 else 2
        }
    } else {
        List(cardCount) { 1 }
    }
}

@Composable
private fun VitalityLandingCard(
    fitScore: Int,
    steps: Int,
    weightSummary: String,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    dailyRecommendation: DailyRecommendation,
    profileName: String,
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityClockEpochMillis: Long,
    compact: Boolean,
    onStartWorkout: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onOpenWeightDetail: () -> Unit,
    onOpenHealthDataDetail: () -> Unit,
    onLogNutrition: () -> Unit
) {
    val palette = homePalette()

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        val backgroundBrush = if (compact) {
            Brush.verticalGradient(
                colors = listOf(
                    palette.accent.copy(alpha = 0.16f),
                    palette.warm.copy(alpha = 0.12f),
                    palette.surface
                )
            )
        } else {
            Brush.horizontalGradient(
                colors = listOf(
                    palette.accent.copy(alpha = 0.18f),
                    palette.warm.copy(alpha = 0.12f),
                    palette.surface
                )
            )
        }

        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundBrush)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    VitalityHeader(
                        dailyRecommendation = dailyRecommendation,
                        profileName = profileName,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )
                    BoxWithConstraints(
                        modifier = Modifier,
                        contentAlignment = Alignment.TopEnd
                    ) {
                        val ringSize = homeRingSize(compact = true)
                        VitalityScoreRings(
                            fitScore = fitScore,
                            steps = steps,
                            activityMinutesToday = activityMinutesToday,
                            heartRateBpm = heartRateBpm,
                            onOpenHealthDataDetail = onOpenHealthDataDetail,
                            ringSize = ringSize,
                            modifier = Modifier.size(ringSize)
                        )
                    }
                }
                VitalityInsightList(insights = dailyRecommendation.insights)
                VitalityActions(
                    compact = true,
                    activityOptions = activityOptions,
                    activeActivity = activeActivity,
                    activityClockEpochMillis = activityClockEpochMillis,
                    onStartWorkout = onStartWorkout,
                    onStopActivity = onStopActivity,
                    onLogNutrition = onLogNutrition
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundBrush)
                    .padding(28.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        VitalityHeader(
                            dailyRecommendation = dailyRecommendation,
                            profileName = profileName,
                            compact = false,
                            modifier = Modifier.weight(1f)
                        )
                        BoxWithConstraints(
                            contentAlignment = Alignment.TopEnd
                        ) {
                            val ringSize = homeRingSize(compact = false)
                            VitalityScoreRings(
                                fitScore = fitScore,
                                steps = steps,
                                activityMinutesToday = activityMinutesToday,
                                heartRateBpm = heartRateBpm,
                                onOpenHealthDataDetail = onOpenHealthDataDetail,
                                ringSize = ringSize,
                                modifier = Modifier.size(ringSize)
                            )
                        }
                    }
                    VitalityInsightList(
                        insights = dailyRecommendation.insights,
                        compact = false
                    )
                    VitalityActions(
                        compact = false,
                        activityOptions = activityOptions,
                        activeActivity = activeActivity,
                        activityClockEpochMillis = activityClockEpochMillis,
                        onStartWorkout = onStartWorkout,
                        onStopActivity = onStopActivity,
                        onLogNutrition = onLogNutrition
                    )
                }
            }
        }
    }
}

@Composable
private fun VitalityHeader(
    dailyRecommendation: DailyRecommendation,
    profileName: String,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val compactSubtitle = "Welkom terug, $profileName."
    val fullSubtitle = "$compactSubtitle ${dailyRecommendation.summary}"
    val compactTitle = dailyRecommendation.title.substringBefore(' ').ifBlank { dailyRecommendation.title }
    val titleStyle = if (compact) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displaySmall
    val subtitleStyle = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge
    val guidanceStyle = if (compact) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
    ) {
        Text(
            text = "TODAY",
            style = MaterialTheme.typography.labelLarge,
            color = palette.textSecondary
        )
        Text(
            text = if (compact) compactTitle else dailyRecommendation.title,
            style = titleStyle,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = if (compact) TextOverflow.Ellipsis else TextOverflow.Clip
        )
        Text(
            text = if (compact) compactSubtitle else fullSubtitle,
            style = subtitleStyle,
            color = palette.textSecondary
        )
        Text(
            text = dailyRecommendation.guidance,
            style = guidanceStyle,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = if (compact) 2 else Int.MAX_VALUE,
            overflow = if (compact) TextOverflow.Ellipsis else TextOverflow.Clip
        )
    }
}

@Composable
private fun VitalityScoreRings(
    fitScore: Int,
    steps: Int,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    onOpenHealthDataDetail: () -> Unit,
    ringSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val vitalityProgress = fitScore.coerceIn(0, 100) / 100f
    val movementProgress = (steps / 6000f).coerceIn(0f, 1f)
    val recoveryProgress = ((84 - heartRateBpm).coerceIn(0, 28) / 28f).coerceIn(0f, 1f)
    val centerSize = homeRingCenterSize(ringSize)

    HomeRingCluster(
        progressValues = listOf(vitalityProgress, movementProgress, recoveryProgress),
        colors = listOf(palette.accent, palette.warm, palette.warning),
        trackColors = listOf(
            palette.accentSoft.copy(alpha = 0.75f),
            palette.warmSoft.copy(alpha = 0.75f),
            palette.warningSoft.copy(alpha = 0.75f)
        ),
        modifier = modifier,
        onClick = onOpenHealthDataDetail,
        centerSizeFraction = centerSize / ringSize,
        strokeWidthPx = homeRingStrokeWidth(ringSize),
        ringGapPx = homeRingGap(ringSize),
        centerState = HomeRingCenterState(
            heartProgress = vitalityProgress,
            scoreText = fitScore.coerceIn(0, 100).toString(),
            heartSizeFraction = 0.78f,
            scoreTextStyle = MaterialTheme.typography.headlineMedium
        )
    )
}

private fun homeRingSize(compact: Boolean): androidx.compose.ui.unit.Dp =
    if (compact) 104.dp else 144.dp

private fun homeRingCenterSize(ringSize: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp =
    (ringSize * 0.38f).coerceIn(46.dp, 58.dp)

private fun homeRingStrokeWidth(ringSize: androidx.compose.ui.unit.Dp): Float =
    if (ringSize <= 120.dp) 8f else 10f

private fun homeRingGap(ringSize: androidx.compose.ui.unit.Dp): Float =
    if (ringSize <= 120.dp) 4f else 5f

@Composable
private fun VitalityInsightList(
    insights: List<DailyRecommendationInsight>,
    compact: Boolean = true
) {
    val palette = homePalette()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Inzichten van vandaag",
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        if (compact) {
            insights.take(3).forEach { insight ->
                VitalityInsightCard(
                    insight = insight,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                insights.take(3).forEach { insight ->
                    VitalityInsightCard(
                        insight = insight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun VitalityInsightCard(
    insight: DailyRecommendationInsight,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val accent = when (insight.tone) {
        RecommendationInsightTone.ACCENT -> palette.accent
        RecommendationInsightTone.WARM -> palette.warm
        RecommendationInsightTone.WARNING -> palette.warning
    }

    Surface(
        modifier = modifier,
        color = palette.surface,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Text(
                text = insight.title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }
    }
}

@Composable
private fun VitalityActions(
    compact: Boolean,
    activityOptions: List<QuickActivityType>,
    activeActivity: ActiveQuickActivitySession?,
    activityClockEpochMillis: Long,
    onStartWorkout: (QuickActivityType) -> Unit,
    onStopActivity: () -> Unit,
    onLogNutrition: () -> Unit
) {
    val workoutOptions = activityOptions.filter { it != QuickActivityType.NUTRITION }
    val palette = homePalette()

    if (compact) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (activeActivity != null) {
                ActiveActivityCard(
                    session = activeActivity,
                    nowEpochMillis = activityClockEpochMillis,
                    onStopActivity = onStopActivity
                )
            }
            ActivityCarousel(
                options = workoutOptions,
                accentColor = palette.accent,
                onSelected = onStartWorkout
            )
            HomeActionButton(
                text = "Log nutrition",
                containerColor = palette.warm,
                contentColor = palette.textPrimary,
                onClick = onLogNutrition,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activeActivity != null) {
                    ActiveActivityCard(
                        session = activeActivity,
                        nowEpochMillis = activityClockEpochMillis,
                        onStopActivity = onStopActivity
                    )
                }
                ActivityCarousel(
                    options = workoutOptions,
                    accentColor = palette.accent,
                    onSelected = onStartWorkout
                )
            }
            HomeActionButton(
                text = "Log nutrition",
                containerColor = palette.warm,
                contentColor = palette.textPrimary,
                onClick = onLogNutrition,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActivityCarousel(
    options: List<QuickActivityType>,
    accentColor: Color,
    onSelected: (QuickActivityType) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onSelected(option) },
                color = palette.surface,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 2.dp,
                    color = accentColor.copy(alpha = 0.72f)
                )
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QuickActivityIcon(
                        type = option,
                        color = accentColor,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val variant = if (containerColor == homePalette().warm) PhButtonVariant.Warning else PhButtonVariant.Primary
    PhButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = variant
    )
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
private fun QuickActivityLogCard(
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    onOpenFitnessDetail: () -> Unit,
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
        PhButton(
            text = if (selectedType == QuickActivityType.FITNESS) {
                "Open fitness detail"
            } else {
                "Log ${selectedType.label.lowercase()}"
            },
            onClick = {
                if (selectedType == QuickActivityType.FITNESS) {
                    onOpenFitnessDetail()
                } else {
                    onLogActivity(selectedType)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            variant = PhButtonVariant.Primary
        )
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
