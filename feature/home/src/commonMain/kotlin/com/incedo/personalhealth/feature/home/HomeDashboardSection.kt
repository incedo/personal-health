package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

        }
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
                VitalityHeader(
                    dailyRecommendation = dailyRecommendation,
                    profileName = profileName,
                    compact = true
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    VitalityScoreRings(
                        fitScore = fitScore,
                        activityMinutesToday = activityMinutesToday,
                        heartRateBpm = heartRateBpm,
                        onOpenHealthDataDetail = onOpenHealthDataDetail,
                        modifier = Modifier.size(240.dp)
                    )
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
                    VitalityHeader(
                        dailyRecommendation = dailyRecommendation,
                        profileName = profileName,
                        compact = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        VitalityScoreRings(
                            fitScore = fitScore,
                            activityMinutesToday = activityMinutesToday,
                            heartRateBpm = heartRateBpm,
                            onOpenHealthDataDetail = onOpenHealthDataDetail,
                            modifier = Modifier.size(280.dp)
                        )
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

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "TODAY",
            style = MaterialTheme.typography.labelLarge,
            color = palette.textSecondary
        )
        Text(
            text = dailyRecommendation.title,
            style = MaterialTheme.typography.displaySmall,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Welkom terug, $profileName. ${dailyRecommendation.summary}",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        Text(
            text = dailyRecommendation.guidance,
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun VitalityScoreRings(
    fitScore: Int,
    activityMinutesToday: Int,
    heartRateBpm: Int,
    onOpenHealthDataDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val vitalityProgress = fitScore.coerceIn(0, 100) / 100f
    val movementProgress = (activityMinutesToday / 60f).coerceIn(0f, 1f)
    val recoveryProgress = ((84 - heartRateBpm).coerceIn(0, 28) / 28f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onOpenHealthDataDetail),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ringGap = 24.dp.toPx()
            val outerStroke = 18.dp.toPx()

            drawRing(
                progress = vitalityProgress,
                color = palette.accent,
                trackColor = palette.accentSoft.copy(alpha = 0.6f),
                strokeWidth = outerStroke,
                inset = 0f
            )
            drawRing(
                progress = movementProgress,
                color = palette.warm,
                trackColor = palette.warmSoft.copy(alpha = 0.7f),
                strokeWidth = outerStroke,
                inset = ringGap
            )
            drawRing(
                progress = recoveryProgress,
                color = palette.warning,
                trackColor = palette.warningSoft.copy(alpha = 0.7f),
                strokeWidth = outerStroke,
                inset = ringGap * 2
            )
        }

        Surface(
            modifier = Modifier.size(148.dp),
            shape = CircleShape,
            color = palette.surface,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    VitalityHeart(
                        progress = vitalityProgress,
                        modifier = Modifier.size(92.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.34f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = fitScore.coerceIn(0, 100).toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VitalityHeart(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val clampedProgress = progress.coerceIn(0f, 1f)
    val heartTrack = palette.warningSoft.copy(alpha = 0.95f)
    val heartFill = palette.warning
    val heartStroke = palette.warning.copy(alpha = 0.9f)

    Canvas(modifier = modifier) {
        val heartPath = Path().apply {
            val width = size.width
            val height = size.height

            moveTo(width * 0.5f, height * 0.92f)
            cubicTo(
                width * 0.1f,
                height * 0.68f,
                width * 0.02f,
                height * 0.34f,
                width * 0.28f,
                height * 0.2f
            )
            cubicTo(
                width * 0.43f,
                height * 0.11f,
                width * 0.5f,
                height * 0.19f,
                width * 0.5f,
                height * 0.28f
            )
            cubicTo(
                width * 0.5f,
                height * 0.19f,
                width * 0.57f,
                height * 0.11f,
                width * 0.72f,
                height * 0.2f
            )
            cubicTo(
                width * 0.98f,
                height * 0.34f,
                width * 0.9f,
                height * 0.68f,
                width * 0.5f,
                height * 0.92f
            )
            close()
        }

        drawPath(
            path = heartPath,
            color = heartTrack
        )

        clipPath(heartPath) {
            val fillHeight = size.height * clampedProgress
            drawRect(
                color = heartFill,
                topLeft = Offset(x = 0f, y = size.height - fillHeight),
                size = Size(width = size.width, height = fillHeight)
            )
        }

        drawPath(
            path = heartPath,
            color = heartStroke,
            style = Stroke(width = size.minDimension * 0.06f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRing(
    progress: Float,
    color: Color,
    trackColor: Color,
    strokeWidth: Float,
    inset: Float
) {
    drawArc(
        color = trackColor,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
        size = androidx.compose.ui.geometry.Size(
            width = size.width - inset * 2,
            height = size.height - inset * 2
        ),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
    drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle = 360f * progress,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
        size = androidx.compose.ui.geometry.Size(
            width = size.width - inset * 2,
            height = size.height - inset * 2
        ),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

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
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 6.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
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
        Button(
            onClick = {
                if (selectedType == QuickActivityType.FITNESS) {
                    onOpenFitnessDetail()
                } else {
                    onLogActivity(selectedType)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.buttonContent
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = if (selectedType == QuickActivityType.FITNESS) {
                    "Open fitness detail"
                } else {
                    "Log ${selectedType.label.lowercase()}"
                },
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
