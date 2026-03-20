package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

internal sealed interface LogbookFeedItem {
    data class Activity(val entry: QuickActivityEntry) : LogbookFeedItem
    data class Nutrition(val entry: NutritionLogEntry) : LogbookFeedItem
}

internal fun buildLogbookFeedItems(
    activityEntries: List<QuickActivityEntry>,
    nutritionEntries: List<NutritionLogEntry>
): List<LogbookFeedItem> {
    val activityFeed = activityEntries.map { entry ->
        entry.createdAtEpochMillis to LogbookFeedItem.Activity(entry)
    }
    val nutritionFeed = nutritionEntries.map { entry ->
        entry.createdAtEpochMillis to LogbookFeedItem.Nutrition(entry)
    }
    return (activityFeed + nutritionFeed)
        .sortedByDescending { it.first }
        .map { it.second }
}

@Composable
internal fun NutritionLogCard(
    entry: NutritionLogEntry,
    onSaveEntry: (NutritionLogEntry) -> Unit
) {
    val palette = homePalette()
    var draftDetails by remember(entry) { mutableStateOf(entry.details) }
    val details = draftDetails
    var recipeExpanded by remember(entry.id) { mutableStateOf(false) }
    var nutritionValuesExpanded by remember(entry.id) { mutableStateOf(false) }
    var editMode by remember(entry.id) { mutableStateOf(false) }

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
                val photos = details.displayPhotos()
                if (editMode) {
                    NutritionPhotoEditorRow(
                        photos = photos,
                        onPhotosChange = { updated ->
                            draftDetails = draftDetails.withUpdatedPhotos(updated)
                        }
                    )
                } else {
                    photos.forEachIndexed { photoIndex, photo ->
                        NutritionPhotoTile(
                            photo = photo,
                            index = photoIndex
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MiniActionChip(
                            label = if (editMode) "Annuleer" else "Bewerk",
                            accent = if (editMode) palette.surfaceMuted else palette.accent,
                            filled = !editMode,
                            onClick = {
                                if (editMode) {
                                    draftDetails = entry.details
                                }
                                editMode = !editMode
                            }
                        )
                        if (editMode) {
                            MiniActionChip(
                                label = "Opslaan",
                                accent = palette.accent,
                                filled = true,
                                onClick = {
                                    onSaveEntry(entry.copy(details = draftDetails))
                                    editMode = false
                                }
                            )
                        }
                    }
                }
                if (editMode) {
                    OutlinedTextField(
                        value = details.note,
                        onValueChange = { draftDetails = draftDetails.copy(note = it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Omschrijving") },
                        minLines = 2
                    )
                } else {
                    Text(
                        text = details.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { recipeExpanded = !recipeExpanded }
                            .padding(horizontal = 2.dp, vertical = 2.dp),
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
                                text = "${details.recipeSections.size} recepten",
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (recipeExpanded) "Sluit" else "Open",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.accent,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (recipeExpanded) "v" else ">",
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.accent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (recipeExpanded) {
                        NutritionRecipeSection(
                            details = details,
                            editMode = editMode,
                            onDetailsChange = { draftDetails = it }
                        )
                    }
                }
            }

            NutritionValuesSection(
                macroMetrics = details.macroMetrics,
                microMetrics = details.microMetrics,
                expanded = nutritionValuesExpanded,
                onToggleExpanded = { nutritionValuesExpanded = !nutritionValuesExpanded }
            )
        }
    }
}

@Composable
private fun MiniActionChip(
    label: String,
    accent: Color,
    filled: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (filled) accent else accent.copy(alpha = 0.14f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (filled) accent else accent.copy(alpha = 0.34f)
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (filled) paletteTextOn(accent) else accent,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun paletteTextOn(color: Color): Color = if ((color.red * 0.299f) + (color.green * 0.587f) + (color.blue * 0.114f) > 0.6f) {
    Color(0xFF0B1F2A)
} else {
    Color.White
}

@Composable
private fun NutritionRecipeSection(
    details: NutritionLogDetails,
    editMode: Boolean,
    onDetailsChange: (NutritionLogDetails) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        details.recipeSections.forEachIndexed { index, section ->
            NutritionRecipeCourseCard(
                section = section,
                courseIndex = index,
                editMode = editMode,
                onSectionChange = { updatedSection ->
                    onDetailsChange(
                        details.copy(
                            recipeSections = details.recipeSections.mapIndexed { itemIndex, item ->
                                if (itemIndex == index) updatedSection else item
                            }
                        )
                    )
                },
                onRemoveSection = {
                    onDetailsChange(
                        details.copy(
                            recipeSections = details.recipeSections.filterIndexed { itemIndex, _ ->
                                itemIndex != index
                            }
                        )
                    )
                }
            )
        }
        if (editMode) {
            MiniActionChip(
                label = "Gang toevoegen",
                accent = homePalette().accent,
                onClick = {
                    onDetailsChange(
                        details.copy(
                            recipeSections = details.recipeSections + NutritionRecipeSection(
                                title = "Gang ${details.recipeSections.size + 1}",
                                sourceUrl = "https://",
                                ingredients = listOf("Nieuw ingrediënt"),
                                steps = listOf("Nieuwe stap")
                            )
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun NutritionRecipeCourseCard(
    section: NutritionRecipeSection,
    courseIndex: Int,
    editMode: Boolean,
    onSectionChange: (NutritionRecipeSection) -> Unit,
    onRemoveSection: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NutritionRecipeHeader(
                title = section.title.ifBlank { "Gang ${courseIndex + 1}" },
                subtitle = section.ingredients.firstOrNull() ?: "Recipe section"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (editMode) {
                    OutlinedTextField(
                        value = section.title,
                        onValueChange = { onSectionChange(section.copy(title = it)) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Gang") },
                        singleLine = true
                    )
                } else {
                    Text(
                        text = section.title.ifBlank { "Gang ${courseIndex + 1}" },
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (editMode && courseIndex > 0) {
                    MiniActionChip(
                        label = "Verwijder",
                        accent = palette.warning,
                        onClick = onRemoveSection
                    )
                }
            }

            BoxWithConstraints {
                val stacked = maxWidth < 760.dp
                if (stacked) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        NutritionIngredientsCard(
                            section = section,
                            editMode = editMode,
                            onSectionChange = onSectionChange
                        )
                        NutritionStepsCard(
                            section = section,
                            editMode = editMode,
                            onSectionChange = onSectionChange
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(0.44f)) {
                            NutritionIngredientsCard(
                                section = section,
                                editMode = editMode,
                                onSectionChange = onSectionChange
                            )
                        }
                        Box(modifier = Modifier.weight(0.56f)) {
                            NutritionStepsCard(
                                section = section,
                                editMode = editMode,
                                onSectionChange = onSectionChange
                            )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (editMode) {
                        OutlinedTextField(
                            value = section.sourceUrl,
                            onValueChange = { onSectionChange(section.copy(sourceUrl = it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Url") },
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = "Oorspronkelijke url",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.textSecondary
                        )
                        Text(
                            text = section.sourceUrl,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.accent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionRecipeHeader(
    title: String,
    subtitle: String
) {
    val palette = homePalette()
    val brush = Brush.linearGradient(
        colors = listOf(
            palette.accent.copy(alpha = 0.92f),
            palette.warm.copy(alpha = 0.86f),
            palette.warning.copy(alpha = 0.82f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(brush)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.88f)
            )
        }
    }
}

@Composable
private fun NutritionIngredientsCard(
    section: NutritionRecipeSection,
    editMode: Boolean,
    onSectionChange: (NutritionRecipeSection) -> Unit
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
            section.ingredients.forEachIndexed { index, ingredient ->
                if (editMode) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = ingredient,
                            onValueChange = { updated ->
                                onSectionChange(
                                    section.copy(
                                        ingredients = section.ingredients.mapIndexed { itemIndex, item ->
                                            if (itemIndex == index) updated else item
                                        }
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        MiniActionChip(
                            label = "Weg",
                            accent = palette.warning,
                            onClick = {
                                onSectionChange(
                                    section.copy(
                                        ingredients = section.ingredients.filterIndexed { itemIndex, _ ->
                                            itemIndex != index
                                        }
                                    )
                                )
                            }
                        )
                    }
                } else {
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
            if (editMode) {
                MiniActionChip(
                    label = "Ingredient toe",
                    accent = palette.accent,
                    onClick = {
                        onSectionChange(section.copy(ingredients = section.ingredients + "Nieuw ingrediënt"))
                    }
                )
            }
        }
    }
}

@Composable
private fun NutritionStepsCard(
    section: NutritionRecipeSection,
    editMode: Boolean,
    onSectionChange: (NutritionRecipeSection) -> Unit
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
            section.steps.forEachIndexed { index, step ->
                if (editMode) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.accent,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = step,
                            onValueChange = { updated ->
                                onSectionChange(
                                    section.copy(
                                        steps = section.steps.mapIndexed { itemIndex, item ->
                                            if (itemIndex == index) updated else item
                                        }
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            minLines = 2
                        )
                        MiniActionChip(
                            label = "Weg",
                            accent = palette.warning,
                            onClick = {
                                onSectionChange(
                                    section.copy(
                                        steps = section.steps.filterIndexed { itemIndex, _ ->
                                            itemIndex != index
                                        }
                                    )
                                )
                            }
                        )
                    }
                } else {
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
            if (editMode) {
                MiniActionChip(
                    label = "Stap toe",
                    accent = palette.accent,
                    onClick = {
                        onSectionChange(section.copy(steps = section.steps + "Nieuwe stap"))
                    }
                )
            }
        }
    }
}

@Composable
private fun NutritionValuesSection(
    macroMetrics: List<NutritionMetric>,
    microMetrics: List<NutritionMetric>,
    expanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    val palette = homePalette()

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
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onToggleExpanded() }
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Voedingswaarden",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${macroMetrics.size} macro's • ${microMetrics.size} micro's",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "Sluit" else "Open",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.accent,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (expanded) "v" else ">",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.accent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (expanded) {
                NutritionMetricSection(
                    title = "Macro's",
                    metrics = macroMetrics,
                    emphasize = true
                )
                NutritionMetricSection(
                    title = "Micro's",
                    metrics = microMetrics,
                    emphasize = false
                )
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
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (emphasize) palette.surfaceRaised else palette.surface,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (emphasize) palette.accent.copy(alpha = 0.18f) else palette.surfaceMuted
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (metrics.isEmpty()) {
                    Text(
                        text = "Nog geen waarden toegevoegd",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                } else {
                    metrics.forEachIndexed { index, metric ->
                        NutritionMetricRow(
                            metric = metric,
                            emphasize = emphasize
                        )
                        if (index < metrics.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(palette.surfaceMuted.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionPhotoEditorRow(
    photos: List<NutritionPhoto>,
    onPhotosChange: (List<NutritionPhoto>) -> Unit
) {
    val palette = homePalette()

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        photos.forEachIndexed { index, photo ->
            Surface(
                modifier = Modifier.width(188.dp),
                color = palette.surface,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NutritionPhotoTile(
                        photo = photo.copy(caption = photo.caption.ifBlank { "Foto ${index + 1}" }),
                        index = index
                    )
                    OutlinedTextField(
                        value = photo.caption,
                        onValueChange = { updated ->
                            onPhotosChange(
                                photos.mapIndexed { itemIndex, item ->
                                    if (itemIndex == index) item.copy(caption = updated) else item
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Foto") },
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MiniActionChip(
                            label = "Upload",
                            accent = palette.accent,
                            onClick = {
                                NutritionImagePicker.pickImage { uploaded ->
                                    if (uploaded != null) {
                                        onPhotosChange(
                                            photos.mapIndexed { itemIndex, item ->
                                                if (itemIndex == index) {
                                                    item.copy(
                                                        caption = uploaded.suggestedCaption,
                                                        imageDataUrl = uploaded.dataUrl
                                                    )
                                                } else {
                                                    item
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                        MiniActionChip(
                            label = "Verwijder",
                            accent = palette.warning,
                            onClick = {
                                onPhotosChange(
                                    photos.filterIndexed { itemIndex, _ -> itemIndex != index }
                                )
                            }
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.width(188.dp),
            color = palette.surface,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, palette.accent.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 164.dp, height = 76.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium,
                        color = palette.accent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Nieuwe foto",
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                MiniActionChip(
                    label = "Upload foto",
                    accent = palette.accent,
                    onClick = {
                        NutritionImagePicker.pickImage { uploaded ->
                            if (uploaded != null) {
                                onPhotosChange(
                                    photos + NutritionPhoto(
                                        caption = uploaded.suggestedCaption,
                                        imageDataUrl = uploaded.dataUrl
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NutritionMetricRow(
    metric: NutritionMetric,
    emphasize: Boolean
) {
    val palette = homePalette()
    val addedAccent = if (emphasize) palette.accent else palette.warm
    val baseAccent = if (emphasize) palette.accent.copy(alpha = 0.38f) else palette.warm.copy(alpha = 0.38f)
    val baseProgress = nutritionMetricBaseProgress(metric)
    val addedProgress = nutritionMetricAddedProgress(metric)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = metric.label,
            modifier = Modifier.weight(0.18f),
            style = MaterialTheme.typography.labelLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .weight(0.62f)
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(palette.surfaceMuted.copy(alpha = 0.75f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(baseProgress)
                    .clip(RoundedCornerShape(999.dp))
                    .background(baseAccent)
            )
            if (addedProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((baseProgress + addedProgress).coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(999.dp))
                        .background(addedAccent)
                )
            }
        }
        Text(
            text = "${nutritionMetricTotalLabel(metric)} / ${metric.dailyTarget}",
            modifier = Modifier.weight(0.20f),
            style = MaterialTheme.typography.bodySmall,
            color = palette.textSecondary
        )
    }
}

private fun nutritionMetricBaseProgress(metric: NutritionMetric): Float {
    val base = metric.baseValue.metricNumber()
    val target = metric.dailyTarget.metricNumber()
    if (target <= 0f) return 0f
    return (base / target).coerceIn(0f, 1f)
}

private fun nutritionMetricAddedProgress(metric: NutritionMetric): Float {
    val added = metric.value.metricNumber()
    val target = metric.dailyTarget.metricNumber()
    if (target <= 0f) return 0f
    return (added / target).coerceIn(0f, 1f - nutritionMetricBaseProgress(metric))
}

private fun String.metricSuffix(): String {
    val numeric = trim().takeWhile { it.isDigit() || it == '.' || it == ',' }
    return trim().removePrefix(numeric).trim()
}

private fun formatMetricNumber(value: Float): String = if (value % 1f == 0f) {
    value.toInt().toString()
} else {
    value.toString()
}

private fun nutritionMetricTotalLabel(metric: NutritionMetric): String {
    val total = metric.baseValue.metricNumber() + metric.value.metricNumber()
    val suffix = metric.dailyTarget.metricSuffix()
    val rendered = formatMetricNumber(total)
    return if (suffix.isBlank()) rendered else "$rendered $suffix"
}

private fun String.metricNumber(): Float {
    val normalized = filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
    return normalized.toFloatOrNull() ?: 0f
}

@Composable
private fun NutritionPhotoTile(
    photo: NutritionPhoto,
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

    val imageBitmap = remember(photo.imageDataUrl) { decodeNutritionPhotoBitmap(photo.imageDataUrl) }

    Box(
        modifier = Modifier
            .size(width = 170.dp, height = 138.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush),
        contentAlignment = Alignment.BottomStart
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = photo.caption,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }
        Text(
            text = photo.caption,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun NutritionLogDetails.displayPhotos(): List<NutritionPhoto> = if (photos.isNotEmpty()) {
    photos
} else {
    photoCaptions.map { caption -> NutritionPhoto(caption = caption) }
}

private fun NutritionLogDetails.withUpdatedPhotos(updated: List<NutritionPhoto>): NutritionLogDetails = copy(
    photos = updated,
    photoCaptions = updated.map { it.caption }
)
