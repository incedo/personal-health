package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.home.FitnessActivitySession
import com.incedo.personalhealth.feature.home.FitnessExercise
import com.incedo.personalhealth.feature.home.NutritionLogDetails
import com.incedo.personalhealth.feature.home.NutritionLogEntry
import com.incedo.personalhealth.feature.home.NutritionMetric
import com.incedo.personalhealth.feature.home.NutritionPhoto
import com.incedo.personalhealth.feature.home.NutritionRecipeSection

internal fun demoFitnessSessions(dayStartEpochMillis: Long): List<FitnessActivitySession> = listOf(
    FitnessActivitySession(
        id = "web-strength-01",
        title = "Upper Body Strength",
        startedAtEpochMillis = dayStartEpochMillis + 6L * HOUR_MILLIS + 30L * MINUTE_MILLIS,
        completedAtEpochMillis = dayStartEpochMillis + 7L * HOUR_MILLIS + 18L * MINUTE_MILLIS,
        notes = "Controlled tempo with progressive overload.",
        primaryMuscleGroupId = "chest",
        exercises = listOf(
            FitnessExercise("bench-press", "Bench Press", 4, 8, 72, primaryMuscleGroupId = "chest"),
            FitnessExercise("seated-row", "Seated Row", 4, 10, 58, primaryMuscleGroupId = "back")
        )
    ),
    FitnessActivitySession(
        id = "web-legs-01",
        title = "Leg Day Builder",
        startedAtEpochMillis = dayStartEpochMillis - DAY_MILLIS + 17L * HOUR_MILLIS,
        completedAtEpochMillis = dayStartEpochMillis - DAY_MILLIS + 18L * HOUR_MILLIS + 5L * MINUTE_MILLIS,
        notes = "Volume block for quads and glutes.",
        primaryMuscleGroupId = "legs",
        exercises = listOf(
            FitnessExercise("back-squat", "Back Squat", 5, 5, 96, primaryMuscleGroupId = "legs"),
            FitnessExercise("romanian-deadlift", "Romanian Deadlift", 4, 8, 82, primaryMuscleGroupId = "legs")
        )
    )
)

internal fun demoNutritionEntries(dayStartEpochMillis: Long): List<NutritionLogEntry> = listOf(
    NutritionLogEntry(
        id = "web-nutrition-01",
        createdAtEpochMillis = dayStartEpochMillis + 8L * HOUR_MILLIS + 10L * MINUTE_MILLIS,
        title = "Recovery breakfast",
        details = NutritionLogDetails(
            posterName = "Kees",
            posterHandle = "@kees",
            note = "Protein oats with berries and Greek yogurt after the morning session.",
            photos = listOf(NutritionPhoto(caption = "Breakfast bowl")),
            photoCaptions = listOf("Oats, yogurt, berries, chia"),
            macroMetrics = listOf(
                NutritionMetric("Protein", "38 g", "130 g", "0 g"),
                NutritionMetric("Carbs", "62 g", "240 g", "0 g"),
                NutritionMetric("Fat", "14 g", "70 g", "0 g")
            ),
            microMetrics = listOf(
                NutritionMetric("Fiber", "11 g", "30 g", "0 g"),
                NutritionMetric("Calcium", "410 mg", "1000 mg", "0 mg")
            ),
            recipeSections = listOf(
                NutritionRecipeSection(
                    title = "How it was made",
                    sourceUrl = "https://example.com/recovery-breakfast",
                    ingredients = listOf("80 g oats", "250 g Greek yogurt", "1 banana", "berries", "chia"),
                    steps = listOf("Cook oats", "Top with yogurt and fruit", "Finish with chia")
                )
            )
        )
    ),
    NutritionLogEntry(
        id = "web-nutrition-02",
        createdAtEpochMillis = dayStartEpochMillis + 13L * HOUR_MILLIS + 5L * MINUTE_MILLIS,
        title = "Lunch prep bowl",
        details = NutritionLogDetails(
            posterName = "Kees",
            posterHandle = "@kees",
            note = "Chicken rice bowl prepared for stable energy through the afternoon.",
            photos = listOf(NutritionPhoto(caption = "Lunch bowl")),
            photoCaptions = listOf("Chicken, rice, greens, avocado"),
            macroMetrics = listOf(
                NutritionMetric("Protein", "46 g", "130 g", "0 g"),
                NutritionMetric("Carbs", "58 g", "240 g", "0 g"),
                NutritionMetric("Fat", "18 g", "70 g", "0 g")
            ),
            microMetrics = listOf(
                NutritionMetric("Potassium", "980 mg", "3500 mg", "0 mg"),
                NutritionMetric("Iron", "4.8 mg", "14 mg", "0 mg")
            ),
            recipeSections = listOf(
                NutritionRecipeSection(
                    title = "Prep flow",
                    sourceUrl = "https://example.com/lunch-bowl",
                    ingredients = listOf("150 g chicken", "180 g rice", "greens", "avocado", "olive oil"),
                    steps = listOf("Cook rice", "Grill chicken", "Assemble bowl", "Finish with greens and avocado")
                )
            )
        )
    )
)

private const val MINUTE_MILLIS = 60_000L
private const val HOUR_MILLIS = 60L * MINUTE_MILLIS
private const val DAY_MILLIS = 24L * HOUR_MILLIS
