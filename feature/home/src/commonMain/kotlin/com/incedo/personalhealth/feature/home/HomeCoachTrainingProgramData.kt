package com.incedo.personalhealth.feature.home

import androidx.compose.ui.graphics.Color
import com.incedo.personalhealth.core.goals.CoachProtocol
import com.incedo.personalhealth.core.goals.CoachProtocolId

internal data class PlanDay(
    val label: String,
    val date: String,
    val kind: PlanDayKind,
    val title: String,
    val meta: String,
    val done: Boolean = false,
    val today: Boolean = false,
    val status: String = "Gepland"
)

internal data class TodayBlock(
    val name: String,
    val sets: String,
    val load: String,
    val note: String
)

internal data class VolumeTarget(
    val name: String,
    val current: Int,
    val max: Int,
    val color: Color
)

internal enum class PlanDayKind(val short: String) {
    Strength("ST"), Cardio("Z2"), Mobility("MO"), Rest("RT")
}

internal fun weekPlanDays(protocol: CoachProtocol): List<PlanDay> = when (protocol.id) {
    CoachProtocolId.RECOVERY -> listOf(
        PlanDay("Ma", "28", PlanDayKind.Mobility, "Mobiliteit", "20 min", done = true),
        PlanDay("Di", "29", PlanDayKind.Cardio, "Z2 walk", "35 min", done = true),
        PlanDay("Wo", "30", PlanDayKind.Rest, "Rust", "Slaapdruk"),
        PlanDay("Do", "1", PlanDayKind.Strength, "Lichte full-body", "35 min", done = true),
        PlanDay("Vr", "2", PlanDayKind.Mobility, "Adem + reset", "20 min", today = true),
        PlanDay("Za", "3", PlanDayKind.Cardio, "Lange wandeling", "60 min"),
        PlanDay("Zo", "4", PlanDayKind.Rest, "Rust", "Deload")
    )
    else -> listOf(
        PlanDay("Ma", "28", PlanDayKind.Strength, "Push", "60 min", done = true),
        PlanDay("Di", "29", PlanDayKind.Cardio, "Zone 2", "45 min", done = true),
        PlanDay("Wo", "30", PlanDayKind.Rest, "Rust", "Wandelen", done = true),
        PlanDay("Do", "1", PlanDayKind.Strength, "Pull", "60 min", done = true),
        PlanDay("Vr", "2", PlanDayKind.Strength, trainingFocusLabel(protocol), "60 min", today = true),
        PlanDay("Za", "3", PlanDayKind.Cardio, "Long ride", "1u 40m"),
        PlanDay("Zo", "4", PlanDayKind.Mobility, "Mobility", "20 min")
    )
}

internal fun todaySessionBlocks(protocol: CoachProtocol): List<TodayBlock> = when (protocol.id) {
    CoachProtocolId.BALANCE -> listOf(
        TodayBlock("Zone 2", "35m", "Z2", "Rustige hartslag, neusademhaling als cue."),
        TodayBlock("Full-body primer", "3x8", "licht", "Compound bewegingen zonder hoge stress."),
        TodayBlock("Mobility flow", "12m", "-", "Heupen, T-spine en ademhaling.")
    )
    CoachProtocolId.RECOVERY -> listOf(
        TodayBlock("Breathing reset", "5m", "-", "Lage belasting om parasympathisch te landen."),
        TodayBlock("Mobility circuit", "3 rondes", "-", "Pijnvrij bereik, geen intensiteit najagen."),
        TodayBlock("Walk", "25m", "Z1", "Alleen als slaapdruk en energie stabiel voelen.")
    )
    else -> listOf(
        TodayBlock("Bench press", "4x5", "75 kg", "PR poging op set 4."),
        TodayBlock("Overhead press", "3x6", "45 kg", "Pauze 2:00."),
        TodayBlock("Incline DB press", "3x10", "22 kg", "RIR 2."),
        TodayBlock("Lateral raise", "3x12", "10 kg", "Strict tempo.")
    )
}

internal fun volumeTargets(protocol: CoachProtocol): List<VolumeTarget> = when (protocol.id) {
    CoachProtocolId.RECOVERY -> listOf(
        VolumeTarget("Herstel", 8, 10, Color(0xFF6B8A4F)),
        VolumeTarget("Mobiliteit", 7, 8, Color(0xFFB5884A)),
        VolumeTarget("Kracht", 4, 8, Color(0xFFC7873A))
    )
    else -> listOf(
        VolumeTarget("Borst", 14, 18, Color(0xFFC7873A)),
        VolumeTarget("Rug", 16, 20, Color(0xFF5C7FA3)),
        VolumeTarget("Benen", 12, 16, Color(0xFF6B8A4F)),
        VolumeTarget("Schouders", 10, 12, Color(0xFFB5884A))
    )
}

internal fun trainingCadenceLabel(protocol: CoachProtocol): String = when (protocol.id) {
    CoachProtocolId.BALANCE -> "3 bewegingblokken + dagelijkse walk"
    CoachProtocolId.COMPOSITION -> "3 krachtmomenten + 2 lichte dagen"
    CoachProtocolId.PERFORMANCE -> "4 gerichte trainingsmomenten"
    CoachProtocolId.RECOVERY -> "2 lichte sessies + herstelwandelingen"
}

internal fun trainingFocusLabel(protocol: CoachProtocol): String = when (protocol.id) {
    CoachProtocolId.BALANCE -> "Consistentie en ritme"
    CoachProtocolId.COMPOSITION -> "Kracht en body composition"
    CoachProtocolId.PERFORMANCE -> "Prestatie en compliance"
    CoachProtocolId.RECOVERY -> "Herstel en rustige opbouw"
}
