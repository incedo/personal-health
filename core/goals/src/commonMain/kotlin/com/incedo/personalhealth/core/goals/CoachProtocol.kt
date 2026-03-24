package com.incedo.personalhealth.core.goals

enum class CoachProtocolId {
    BALANCE,
    COMPOSITION,
    PERFORMANCE,
    RECOVERY
}

data class CoachProtocol(
    val id: CoachProtocolId,
    val title: String,
    val summary: String,
    val rhythm: String,
    val anchors: List<String>
)

val coachProtocols: List<CoachProtocol> = listOf(
    CoachProtocol(
        id = CoachProtocolId.BALANCE,
        title = "Circadian Focus",
        summary = "Een protocol rond daglicht, slaapdruk, cafeine-timing en focusblokken met dagelijkse beweging als basis.",
        rhythm = "Ochtendlicht, cafeine later op de ochtend, werken in focusvensters en een vaste avondafbouw.",
        anchors = listOf("10 min ochtendlicht", "Cafeine na 90 min", "Vaste bedtijd", "Dagelijkse wandeling")
    ),
    CoachProtocol(
        id = CoachProtocolId.COMPOSITION,
        title = "Metabolic Lean",
        summary = "Een protocol voor body composition met voedingstiming, eiwit, glucosestabiliteit en krachttraining.",
        rhythm = "Eiwitrijke maaltijden, loggen van voeding, trainen met intentie en lichaamsdata actief volgen.",
        anchors = listOf("3x krachttraining", "Eiwit per maaltijd", "Laat eten beperken", "Gewicht en taille volgen")
    ),
    CoachProtocol(
        id = CoachProtocolId.PERFORMANCE,
        title = "Blueprint Discipline",
        summary = "Een strak protocol met vaste routines, veel meten, hoge compliance en herstel als dagelijks systeem.",
        rhythm = "Een voorspelbare dagstructuur met meetmomenten, vaste eetvensters en weinig ruis in gedrag.",
        anchors = listOf("Vaste routines", "Meetwaarden checken", "Voeding vooraf plannen", "Herstel elke avond evalueren")
    ),
    CoachProtocol(
        id = CoachProtocolId.RECOVERY,
        title = "Recovery Reset",
        summary = "Een zachter herstelprotocol voor stressregulatie, betere slaap en rustig opgebouwde activiteit.",
        rhythm = "Lager tempo, minder prikkels, korte wandelingen en een simpele dagstructuur met vroege avondrust.",
        anchors = listOf("Vaste bedtijd", "Wandeling na lunch", "Schermen eerder uit", "Cafeine later beperken")
    )
)

fun coachProtocolById(id: CoachProtocolId?): CoachProtocol = coachProtocols.firstOrNull { it.id == id }
    ?: coachProtocols.first()
