package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.goals.CoachProtocol
import com.incedo.personalhealth.core.goals.CoachRecommendation

@Composable
internal fun CoachTrainingProgramContent(
    recommendation: CoachRecommendation,
    selectedProtocol: CoachProtocol
) {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Trainingsprogramma",
            style = MaterialTheme.typography.titleLarge,
            color = homePalette().textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Coach koppelt je gekozen protocol direct aan een trainingsritme, zodat je weet hoe vaak, hoe zwaar en met welk doel je deze week traint.",
            style = MaterialTheme.typography.bodyLarge,
            color = homePalette().textSecondary
        )
        Spacer(modifier = Modifier.height(18.dp))
        CoachProgramMetric(
            title = "Actief protocol",
            value = selectedProtocol.title,
            description = selectedProtocol.summary
        )
        Spacer(modifier = Modifier.height(12.dp))
        CoachProgramMetric(
            title = "Weekritme",
            value = trainingCadenceLabel(selectedProtocol),
            description = selectedProtocol.rhythm
        )
        Spacer(modifier = Modifier.height(12.dp))
        CoachProgramMetric(
            title = "Sessie-focus",
            value = trainingFocusLabel(selectedProtocol),
            description = recommendation.rationale.firstOrNull().orEmpty()
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Programma-opbouw",
            style = MaterialTheme.typography.titleLarge,
            color = homePalette().textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            coachProgramBlocks(selectedProtocol).forEach { block ->
                CoachProgramMetric(
                    title = block.title,
                    value = block.value,
                    description = block.description
                )
            }
        }
    }
}

@Composable
private fun CoachProgramMetric(
    title: String,
    value: String,
    description: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = homePalette().textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = homePalette().textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = homePalette().textSecondary
        )
    }
}

private data class CoachProgramBlock(
    val title: String,
    val value: String,
    val description: String
)

private fun trainingCadenceLabel(protocol: CoachProtocol): String = when (protocol.id) {
    com.incedo.personalhealth.core.goals.CoachProtocolId.BALANCE -> "3 bewegingblokken + dagelijkse walk"
    com.incedo.personalhealth.core.goals.CoachProtocolId.COMPOSITION -> "3 krachtmomenten + 2 lichte dagen"
    com.incedo.personalhealth.core.goals.CoachProtocolId.PERFORMANCE -> "4 gerichte trainingsmomenten"
    com.incedo.personalhealth.core.goals.CoachProtocolId.RECOVERY -> "2 lichte sessies + herstelwandelingen"
}

private fun trainingFocusLabel(protocol: CoachProtocol): String = when (protocol.id) {
    com.incedo.personalhealth.core.goals.CoachProtocolId.BALANCE -> "Consistentie en ritme"
    com.incedo.personalhealth.core.goals.CoachProtocolId.COMPOSITION -> "Kracht en body composition"
    com.incedo.personalhealth.core.goals.CoachProtocolId.PERFORMANCE -> "Prestatie en compliance"
    com.incedo.personalhealth.core.goals.CoachProtocolId.RECOVERY -> "Herstel en rustige opbouw"
}

private fun coachProgramBlocks(protocol: CoachProtocol): List<CoachProgramBlock> = when (protocol.id) {
    com.incedo.personalhealth.core.goals.CoachProtocolId.BALANCE -> listOf(
        CoachProgramBlock("Dag 1-2", "Zone 2 + mobiliteit", "Open de week met lage intensiteit en houd de dagstructuur stabiel."),
        CoachProgramBlock("Dag 3-4", "Volledige lichaamssessie", "Een compacte krachtprikkel die past binnen je vaste ritme."),
        CoachProgramBlock("Dag 5-7", "Wandelen en reset", "Gebruik wandelen als anker om herstel en slaapdruk te ondersteunen.")
    )
    com.incedo.personalhealth.core.goals.CoachProtocolId.COMPOSITION -> listOf(
        CoachProgramBlock("Sessie A", "Lower/Push", "Train grote spiergroepen vroeg in de week en log voeding er strak omheen."),
        CoachProgramBlock("Sessie B", "Upper/Pull", "Houd de belasting progressief maar beheerst zodat herstel mee kan komen."),
        CoachProgramBlock("Extra blok", "Steps + core", "Gebruik je niet-krachtdagen voor stappen, core en glucosestabiliteit.")
    )
    com.incedo.personalhealth.core.goals.CoachProtocolId.PERFORMANCE -> listOf(
        CoachProgramBlock("Blok 1", "Power of speed", "Begin fris en plan je zwaarste kwaliteitssessie wanneer compliance het hoogst is."),
        CoachProgramBlock("Blok 2", "Strength repeat", "Herhaal gericht volume met vaste rust- en meetmomenten."),
        CoachProgramBlock("Blok 3", "Deload check", "Evalueer herstel en stuur direct bij op basis van signalen.")
    )
    com.incedo.personalhealth.core.goals.CoachProtocolId.RECOVERY -> listOf(
        CoachProgramBlock("Herstel 1", "Wandeling + mobiliteit", "Hou de belasting laag en laat regelmaat belangrijker zijn dan volume."),
        CoachProgramBlock("Herstel 2", "Lichte full-body", "Een korte sessie om beweging terug op te bouwen zonder stresspiek."),
        CoachProgramBlock("Herstel 3", "Adem + avondrust", "Gebruik je avondroutine als vast onderdeel van het programma.")
    )
}
