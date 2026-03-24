package com.incedo.personalhealth.core.recommendations

data class DailyRecommendationRequest(
    val fitScore: Int,
    val heartRateBpm: Int,
    val steps: Int,
    val activityMinutesToday: Int,
    val profileName: String
)

data class DailyRecommendation(
    val title: String,
    val summary: String,
    val guidance: String,
    val source: RecommendationSource
)

enum class RecommendationSource {
    STUB,
    BACKEND
}

fun defaultDailyRecommendation(
    request: DailyRecommendationRequest
): DailyRecommendation {
    val variant = recommendationVariant(
        request = request,
        variantIndex = 0
    )

    return DailyRecommendation(
        title = "Focus van de dag",
        summary = variant.summary,
        guidance = variant.guidance,
        source = RecommendationSource.STUB
    )
}

internal data class RecommendationVariant(
    val summary: String,
    val guidance: String
)

internal fun recommendationVariant(
    request: DailyRecommendationRequest,
    variantIndex: Int
): RecommendationVariant {
    val summaries = when {
        request.fitScore >= 80 -> listOf(
            "Je totaal staat sterk op ${request.fitScore}. Met ${request.steps} stappen en ${request.activityMinutesToday} actieve minuten ligt je dag goed open.",
            "De buitenste ring staat stevig: totaal ${request.fitScore}, beweging ${request.activityMinutesToday} minuten en ${request.steps} stappen op de teller.",
            "Je dagscore zit op ${request.fitScore}. De ringen laten zien dat je beweging al op gang is met ${request.steps} stappen en ${request.activityMinutesToday} actieve minuten."
        )

        request.fitScore >= 65 -> listOf(
            "Je totaal staat op ${request.fitScore}. De dag is stabiel, met ${request.steps} stappen en ${request.activityMinutesToday} actieve minuten als goede basis.",
            "De ring voor totaal blijft netjes op ${request.fitScore}; met ${request.steps} stappen en ${request.activityMinutesToday} minuten beweging houd je het momentum vast.",
            "Vandaag voelt beheerst: totaal ${request.fitScore}, al ${request.steps} stappen gezet en ${request.activityMinutesToday} actieve minuten opgebouwd."
        )

        else -> listOf(
            "Je totaal zit nu op ${request.fitScore}. Met ${request.steps} stappen en ${request.activityMinutesToday} actieve minuten is dit vooral een dag om slim te doseren.",
            "De buitenste ring vraagt aandacht: totaal ${request.fitScore}, beweging ${request.activityMinutesToday} minuten en ${request.steps} stappen tot nu toe.",
            "Je dagwaarde staat op ${request.fitScore}. De ringen tonen nog beperkte marge met ${request.steps} stappen en ${request.activityMinutesToday} actieve minuten."
        )
    }

    val guidances = when {
        request.heartRateBpm >= 78 -> listOf(
            "Je hartslag zit op ${request.heartRateBpm} bpm. Houd de hart-ring rustig en kies vandaag voor herstel of een korte sessie.",
            "Het hartje loopt met ${request.heartRateBpm} bpm wat hoger, dus laat de herstelring leidend zijn en bouw niet te hard op.",
            "Met ${request.heartRateBpm} bpm vraagt je hartslag om controle: houd de belasting licht en gebruik beweging vooral om te herstellen."
        )

        request.heartRateBpm >= 68 -> listOf(
            "Je hartslag staat op ${request.heartRateBpm} bpm. Prima zone om je ritme vast te houden met een geplande wandeling of training.",
            "Het hartje zit op ${request.heartRateBpm} bpm; dat geeft ruimte om de bewegingsring rustig verder te vullen.",
            "Met ${request.heartRateBpm} bpm blijft je herstel redelijk in balans. Een gecontroleerde sessie past hier goed bij."
        )

        else -> listOf(
            "Je hartslag blijft laag op ${request.heartRateBpm} bpm. Dat geeft ruimte om de dag sterk maar beheerst verder op te bouwen.",
            "Het hartje oogt rustig met ${request.heartRateBpm} bpm, dus je kunt extra kwaliteit uit je beweegring halen zonder te forceren.",
            "Met ${request.heartRateBpm} bpm is je herstelgunstig. Als het goed voelt kun je vandaag nog gericht kwaliteit toevoegen."
        )
    }

    return RecommendationVariant(
        summary = summaries[variantIndex.mod(summaries.size)],
        guidance = guidances[variantIndex.mod(guidances.size)]
    )
}
