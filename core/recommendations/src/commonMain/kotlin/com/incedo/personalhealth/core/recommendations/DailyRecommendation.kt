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
    val insights: List<DailyRecommendationInsight>,
    val source: RecommendationSource
)

data class DailyRecommendationInsight(
    val title: String,
    val description: String,
    val tone: RecommendationInsightTone
)

enum class RecommendationInsightTone {
    ACCENT,
    WARM,
    WARNING
}

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
        insights = recommendationInsights(request, variantIndex = 0),
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

internal fun recommendationInsights(
    request: DailyRecommendationRequest,
    variantIndex: Int
): List<DailyRecommendationInsight> {
    val recoveryInsight = when {
        request.fitScore >= 80 && request.heartRateBpm <= 64 -> listOf(
            DailyRecommendationInsight(
                title = "Herstel sterk",
                description = "Je totaal staat sterk en je hartslag blijft rustig op ${request.heartRateBpm} bpm. Een geplande workout past goed in je dag.",
                tone = RecommendationInsightTone.ACCENT
            ),
            DailyRecommendationInsight(
                title = "Hartslag stabiel",
                description = "Met ${request.heartRateBpm} bpm oogt je herstelring rustig. Je kunt vandaag kwaliteit leveren zonder te forceren.",
                tone = RecommendationInsightTone.ACCENT
            )
        )

        request.heartRateBpm >= 75 -> listOf(
            DailyRecommendationInsight(
                title = "Herstel bewaken",
                description = "Je hartje staat hoger op ${request.heartRateBpm} bpm. Kies vandaag liever voor licht werk of extra rust.",
                tone = RecommendationInsightTone.WARNING
            ),
            DailyRecommendationInsight(
                title = "Belasting doseren",
                description = "De herstelring vraagt aandacht met ${request.heartRateBpm} bpm. Hou de intensiteit laag en bewaak je ritme.",
                tone = RecommendationInsightTone.WARNING
            )
        )

        else -> listOf(
            DailyRecommendationInsight(
                title = "Herstel in balans",
                description = "Je dagstart is stabiel met ${request.heartRateBpm} bpm. Bouw rustig op richting je hoofdactiviteit.",
                tone = RecommendationInsightTone.ACCENT
            ),
            DailyRecommendationInsight(
                title = "Hartslag beheerst",
                description = "Het hartje blijft gecontroleerd op ${request.heartRateBpm} bpm. Dat geeft ruimte voor een nette opbouw.",
                tone = RecommendationInsightTone.ACCENT
            )
        )
    }

    val movementInsight = when {
        request.activityMinutesToday >= 45 -> listOf(
            DailyRecommendationInsight(
                title = "Activiteit op koers",
                description = "Je actieve minuten staan al op ${request.activityMinutesToday} en je stappen op ${request.steps}. Gebruik de rest van de dag om dit ritme vast te houden.",
                tone = RecommendationInsightTone.ACCENT
            ),
            DailyRecommendationInsight(
                title = "Beweegring sterk",
                description = "Met ${request.activityMinutesToday} actieve minuten en ${request.steps} stappen staat je bewegingsring er goed voor.",
                tone = RecommendationInsightTone.ACCENT
            )
        )

        request.activityMinutesToday >= 20 || request.steps >= 4_000 -> listOf(
            DailyRecommendationInsight(
                title = "Activiteit bouwt op",
                description = "Je hebt al ${request.activityMinutesToday} actieve minuten en ${request.steps} stappen. Nog een kort blok tilt je ring zichtbaar verder op.",
                tone = RecommendationInsightTone.WARM
            ),
            DailyRecommendationInsight(
                title = "Ritme groeit",
                description = "De beweegring komt los met ${request.steps} stappen en ${request.activityMinutesToday} minuten. Een extra ronde later vandaag maakt verschil.",
                tone = RecommendationInsightTone.WARM
            )
        )

        else -> listOf(
            DailyRecommendationInsight(
                title = "Meer actieve tijd nodig",
                description = "Start een wandeling, run of gymsessie zodat je bewegingsring met ${request.activityMinutesToday} minuten niet achterblijft.",
                tone = RecommendationInsightTone.WARM
            ),
            DailyRecommendationInsight(
                title = "Beweging nog laag",
                description = "Met ${request.steps} stappen en ${request.activityMinutesToday} actieve minuten is dit een goed moment om je dag fysiek open te breken.",
                tone = RecommendationInsightTone.WARM
            )
        )
    }

    val nutritionInsight = if (request.fitScore >= 75) {
        listOf(
            DailyRecommendationInsight(
                title = "Voeding klaarzetten",
                description = "Je totaal staat op ${request.fitScore}. Log nutrition en mik op eiwit plus langzame koolhydraten om herstel en training te ondersteunen.",
                tone = RecommendationInsightTone.WARM
            ),
            DailyRecommendationInsight(
                title = "Brandstof vasthouden",
                description = "Met een totaalscore van ${request.fitScore} loont het om voeding strak te plannen, zodat je sterke dag niet inzakt.",
                tone = RecommendationInsightTone.WARM
            )
        )
    } else {
        listOf(
            DailyRecommendationInsight(
                title = "Brandstof aanvullen",
                description = "Je totaal staat op ${request.fitScore}. Log nutrition en voeg een eiwitrijke snack of lunch toe om je dag te stabiliseren.",
                tone = RecommendationInsightTone.WARNING
            ),
            DailyRecommendationInsight(
                title = "Voeding eerst",
                description = "Met een totaalscore van ${request.fitScore} helpt snelle, degelijke brandstof meer dan extra intensiteit.",
                tone = RecommendationInsightTone.WARNING
            )
        )
    }

    return listOf(
        recoveryInsight[variantIndex.mod(recoveryInsight.size)],
        movementInsight[variantIndex.mod(movementInsight.size)],
        nutritionInsight[variantIndex.mod(nutritionInsight.size)]
    )
}
