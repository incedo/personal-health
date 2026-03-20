package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    }
}

/*
@Composable
private fun NutritionLogCard(
    entry: NutritionLogEntry
) {
    val palette = homePalette()
    val details = entry.details
    var recipeExpanded by remember(entry.id) { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(palette.warm.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = details.posterName.take(1),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.warm,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = details.posterName,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${details.posterHandle} • eten / drinken",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                details.photoCaptions.forEachIndexed { photoIndex, caption ->
                    NutritionPhotoTile(
                        caption = caption,
                        index = photoIndex
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = details.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }

            NutritionMetricSection(
                title = "Macro's",
                metrics = details.macroMetrics,
                emphasize = true
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = palette.surface,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Recept",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.textSecondary
                            )
                            Text(
                                text = details.recipeTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { recipeExpanded = !recipeExpanded },
                            color = if (recipeExpanded) palette.accent else palette.surfaceRaised,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (recipeExpanded) palette.accent else palette.surfaceMuted
                            )
                        ) {
                            Text(
                                text = if (recipeExpanded) "Sluit recept" else "Open recept",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (recipeExpanded) palette.surface else palette.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    if (recipeExpanded) {
                        NutritionRecipeSection(details = details)
                    } else {
                        Text(
                            text = details.sourceUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.accent
                        )
                    }
                }
            }

            NutritionMetricSection(
                title = "Micro's",
                metrics = details.microMetrics,
                emphasize = false
            )
        }
    }
}

@Composable
private fun NutritionRecipeSection(
    details: NutritionLogDetails
) {
    val palette = homePalette()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BoxWithConstraints {
            val stacked = maxWidth < 760.dp
            if (stacked) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NutritionIngredientsCard(details = details)
                    NutritionStepsCard(details = details)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(0.44f)) {
                        NutritionIngredientsCard(details = details)
                    }
                    Box(modifier = Modifier.weight(0.56f)) {
                        NutritionStepsCard(details = details)
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = palette.warmSoft,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, palette.warm.copy(alpha = 0.24f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Oorspronkelijk recept",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textSecondary
                )
                Text(
                    text = details.sourceUrl,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.accent,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun NutritionIngredientsCard(
    details: NutritionLogDetails
) {
    val palette = homePalette()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Ingrediënten",
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            details.recipeIngredients.forEach { ingredient ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(palette.warm)
                    )
                    Text(
                        text = ingredient,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionStepsCard(
    details: NutritionLogDetails
) {
    val palette = homePalette()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Zo maak je dit",
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            details.recipeSteps.forEachIndexed { index, step ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.accent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionMetricSection(
    title: String,
    metrics: List<NutritionMetric>,
    emphasize: Boolean
) {
    val palette = homePalette()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = palette.textSecondary
        )
        metrics.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { metric ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = if (emphasize) palette.surface else palette.surfaceRaised,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (emphasize) palette.accent.copy(alpha = 0.18f) else palette.surfaceMuted
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = metric.value,
                                style = if (emphasize) {
                                    MaterialTheme.typography.headlineSmall
                                } else {
                                    MaterialTheme.typography.titleLarge
                                },
                                color = if (emphasize) palette.textPrimary else palette.accent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = metric.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.textSecondary
                            )
                        }
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun NutritionPhotoTile(
    caption: String,
    index: Int
) {
    val palette = homePalette()
    val brush = when (index % 3) {
        0 -> androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(palette.warm.copy(alpha = 0.95f), palette.warning.copy(alpha = 0.8f))
        )
        1 -> androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(palette.accent.copy(alpha = 0.95f), palette.warm.copy(alpha = 0.72f))
        )
        else -> androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(palette.warning.copy(alpha = 0.88f), palette.accent.copy(alpha = 0.72f))
        )
    }

    Box(
        modifier = Modifier
            .size(width = 170.dp, height = 138.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = caption,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

*/
@Composable
internal fun ThemeModeCard(
    selectedMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Thema",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Schakel de app tussen systeemstand, dark en light zonder van scherm te wisselen.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeThemeMode.entries.forEach { mode ->
                val selected = mode == selectedMode
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onThemeModeSelected(mode) },
                    color = if (selected) palette.accentSoft else palette.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) palette.accent else palette.surfaceMuted
                    )
                ) {
                    Text(
                        text = mode.label,
                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textPrimary,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
internal fun FitnessBodyProfileCard(
    selectedProfile: FitnessBodyProfile,
    onProfileSelected: (FitnessBodyProfile) -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Lichaamsprofiel",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Kies welk anatomiebeeld standaard gebruikt wordt in de fitnessflow.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FitnessBodyProfile.entries.forEach { profile ->
                val selected = profile == selectedProfile
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onProfileSelected(profile) },
                    color = if (selected) palette.accentSoft else palette.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) palette.accent else palette.surfaceMuted
                    )
                ) {
                    Text(
                        text = if (profile == FitnessBodyProfile.MALE) "Man" else "Vrouw",
                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textPrimary,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfileFitScoreCard(
    fitScore: Int,
    profileName: String,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    HomePanel(modifier = modifier) {
        Text(
            text = "Dagstatus",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Je persoonlijke score en basisstatus op een vaste plek.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ProfileRing(
                fitScore = fitScore,
                profileName = profileName,
                modifier = Modifier.size(220.dp)
            )
        }
    }
}

@Composable
internal fun ProfileRing(
    fitScore: Int,
    profileName: String,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val clamped = fitScore.coerceIn(0, 100)
    val progress = clamped / 100f

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 18.dp.toPx()
            drawArc(
                color = palette.surfaceMuted,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = palette.accent,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(palette.warningSoft.copy(alpha = 0.75f))
                    .border(width = 2.dp, color = palette.warning, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ProfileHeart(
                        progress = progress,
                        modifier = Modifier.size(72.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.34f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = clamped.toString(),
                            style = MaterialTheme.typography.titleLarge,
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
private fun ProfileHeart(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val clampedProgress = progress.coerceIn(0f, 1f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val heartPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(width * 0.5f, height * 0.92f)
            cubicTo(width * 0.1f, height * 0.68f, width * 0.02f, height * 0.34f, width * 0.28f, height * 0.2f)
            cubicTo(width * 0.43f, height * 0.11f, width * 0.5f, height * 0.19f, width * 0.5f, height * 0.28f)
            cubicTo(width * 0.5f, height * 0.19f, width * 0.57f, height * 0.11f, width * 0.72f, height * 0.2f)
            cubicTo(width * 0.98f, height * 0.34f, width * 0.9f, height * 0.68f, width * 0.5f, height * 0.92f)
            close()
        }

        drawPath(
            path = heartPath,
            color = palette.warningSoft.copy(alpha = 0.95f)
        )

        clipPath(heartPath) {
            val fillHeight = size.height * clampedProgress
            drawRect(
                color = palette.warning,
                topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - fillHeight),
                size = androidx.compose.ui.geometry.Size(size.width, fillHeight)
            )
        }

        drawPath(
            path = heartPath,
            color = palette.warning.copy(alpha = 0.9f),
            style = Stroke(width = size.minDimension * 0.07f)
        )
    }
}
