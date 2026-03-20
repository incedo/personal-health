package com.incedo.personalhealth.feature.home

import kotlinx.serialization.Serializable

@Serializable
data class NutritionPhoto(
    val caption: String,
    val imageDataUrl: String? = null
)

@Serializable
data class NutritionMetric(
    val label: String,
    val value: String,
    val dailyTarget: String,
    val baseValue: String
)

@Serializable
data class NutritionRecipeSection(
    val title: String,
    val sourceUrl: String,
    val ingredients: List<String>,
    val steps: List<String>
)

@Serializable
data class NutritionLogDetails(
    val posterName: String,
    val posterHandle: String,
    val note: String,
    val photos: List<NutritionPhoto> = emptyList(),
    val photoCaptions: List<String> = emptyList(),
    val macroMetrics: List<NutritionMetric>,
    val microMetrics: List<NutritionMetric>,
    val recipeSections: List<NutritionRecipeSection>
)

@Serializable
data class NutritionLogEntry(
    val id: String,
    val createdAtEpochMillis: Long,
    val title: String,
    val details: NutritionLogDetails
)

fun createNutritionLogEntry(
    existingEntries: List<NutritionLogEntry>,
    nowEpochMillis: Long
): NutritionLogEntry {
    val nextOrdinal = (existingEntries.size + 1).toString().padStart(2, '0')
    return NutritionLogEntry(
        id = "nutrition-$nextOrdinal",
        createdAtEpochMillis = nowEpochMillis,
        title = "Nutrition log",
        details = emptyNutritionLogDetails()
    )
}

private fun emptyNutritionLogDetails(): NutritionLogDetails = NutritionLogDetails(
    posterName = "Jij",
    posterHandle = "@jij",
    note = "",
    photos = emptyList(),
    macroMetrics = emptyList(),
    microMetrics = emptyList(),
    recipeSections = emptyList()
)
