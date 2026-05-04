package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhButton
import com.incedo.personalhealth.core.designsystem.PhButtonSize
import com.incedo.personalhealth.core.designsystem.PhButtonVariant
import com.incedo.personalhealth.core.designsystem.PhSectionHeader
import com.incedo.personalhealth.core.designsystem.PhTag
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.goals.CoachProtocol
import com.incedo.personalhealth.core.goals.CoachRecommendation

@Composable
internal fun CoachTrainingProgramContent(
    compact: Boolean,
    recommendation: CoachRecommendation,
    selectedProtocol: CoachProtocol
) {
    RecoveryAdaptPanel(recommendation = recommendation, selectedProtocol = selectedProtocol)
    Spacer(modifier = Modifier.height(18.dp))
    if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            WeekPlanPanel(selectedProtocol = selectedProtocol, compact = true)
            TodaySessionPanel(selectedProtocol = selectedProtocol)
            MesocyclePanel(selectedProtocol = selectedProtocol)
            FeelTodayPanel()
            VolumeTargetPanel(selectedProtocol = selectedProtocol)
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            WeekPlanPanel(selectedProtocol = selectedProtocol, compact = false)
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                TodaySessionPanel(selectedProtocol = selectedProtocol, modifier = Modifier.weight(1.4f))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    MesocyclePanel(selectedProtocol = selectedProtocol)
                    FeelTodayPanel()
                    VolumeTargetPanel(selectedProtocol = selectedProtocol)
                }
            }
        }
    }
}

@Composable
private fun RecoveryAdaptPanel(
    recommendation: CoachRecommendation,
    selectedProtocol: CoachProtocol
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth(), contentPadding = 24.dp) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(color = palette.warning, shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = "AI",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.buttonContent,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                PhSectionHeader(kicker = "Recovery adapt", title = "Plan past zich aan") {
                    PhTag("Actief protocol", tone = PhTagTone.Primary)
                }
                Text(
                    text = "Goed hersteld. ${trainingFocusLabel(selectedProtocol)} blijft staan; coach verlaagt volume alleen als herstel terugvalt.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
                recommendation.rationale.firstOrNull()?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekPlanPanel(
    selectedProtocol: CoachProtocol,
    compact: Boolean
) {
    val days = weekPlanDays(selectedProtocol)
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(kicker = "Deze week", title = "Trainingsritme") {
            PhTag("${days.count { it.kind != PlanDayKind.Rest }} sessies", tone = PhTagTone.Info)
        }
        Text(
            text = trainingCadenceLabel(selectedProtocol),
            style = MaterialTheme.typography.bodyMedium,
            color = homePalette().textSecondary,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                days.forEach { PlanDayRow(day = it) }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                days.forEach { PlanDayTile(day = it, modifier = Modifier.weight(1f)) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProgressBar(label = "Belasting deze week", current = 62, max = 80, color = homePalette().warning)
    }
}

@Composable
private fun PlanDayTile(day: PlanDay, modifier: Modifier = Modifier) {
    val palette = homePalette()
    Surface(
        modifier = modifier.height(154.dp),
        color = if (day.today) palette.warningSoft else palette.surfaceMuted,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(if (day.today) 2.dp else 1.dp, if (day.today) palette.warning else Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(day.label, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                Text(day.date, style = MaterialTheme.typography.labelMedium, color = palette.textPrimary, fontWeight = FontWeight.Bold)
            }
            PlanKindMark(day.kind.short, day.kind.accent())
            Text(day.title, style = MaterialTheme.typography.labelLarge, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
            Text(day.meta, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (day.done) "Klaar" else if (day.today) "Vandaag" else day.status,
                style = MaterialTheme.typography.labelSmall,
                color = if (day.today) palette.warning else palette.textSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PlanDayRow(day: PlanDay) {
    val palette = homePalette()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (day.today) palette.warningSoft else palette.surfaceMuted,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(if (day.today) 2.dp else 1.dp, if (day.today) palette.warning else Color.Transparent)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PlanKindMark(day.kind.short, day.kind.accent())
            Column(modifier = Modifier.weight(1f)) {
                Text("${day.label} ${day.date} · ${day.title}", style = MaterialTheme.typography.titleSmall, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(day.meta, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
            }
            PhTag(if (day.today) "Vandaag" else if (day.done) "Klaar" else day.status)
        }
    }
}

@Composable
private fun TodaySessionPanel(selectedProtocol: CoachProtocol, modifier: Modifier = Modifier) {
    HomePanel(modifier = modifier.fillMaxWidth()) {
        PhSectionHeader(kicker = "Vandaag", title = trainingFocusLabel(selectedProtocol)) {
            PhButton("Start", onClick = {}, size = PhButtonSize.Small, variant = PhButtonVariant.Warning)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 14.dp)) {
            SessionMetric("VOL", "12.4 t", Modifier.weight(1f))
            SessionMetric("SETS", "16", Modifier.weight(1f))
            SessionMetric("DUR", "60 min", Modifier.weight(1f))
            SessionMetric("RPE", "7", Modifier.weight(1f))
        }
        todaySessionBlocks(selectedProtocol).forEachIndexed { index, block ->
            SessionBlockRow(index = index + 1, block = block)
        }
    }
}

@Composable
private fun SessionMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = homePalette().surfaceMuted, shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = homePalette().textSecondary)
            Text(value, style = MaterialTheme.typography.titleMedium, color = homePalette().textPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SessionBlockRow(index: Int, block: TodayBlock) {
    val palette = homePalette()
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PlanKindMark(index.toString(), palette.warning)
        Column(modifier = Modifier.weight(1f)) {
            Text(block.name, style = MaterialTheme.typography.titleSmall, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
            Text(block.note, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
        }
        PhTag(block.sets)
        Text(block.load, style = MaterialTheme.typography.labelLarge, color = palette.textSecondary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PlanKindMark(text: String, color: Color) {
    Surface(color = color, shape = RoundedCornerShape(10.dp)) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelSmall,
            color = homePalette().buttonContent,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProgressBar(label: String, current: Int, max: Int, color: Color) {
    val palette = homePalette()
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
        Text("$current / $max", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
    }
    Box(
        modifier = Modifier.fillMaxWidth().height(10.dp).padding(top = 6.dp).clip(RoundedCornerShape(999.dp)).background(palette.surfaceMuted)
    ) {
        Box(modifier = Modifier.fillMaxWidth(current / max.toFloat()).height(10.dp).background(color))
    }
}

@Composable
private fun PlanDayKind.accent(): Color {
    val palette = homePalette()
    return when (this) {
        PlanDayKind.Strength -> palette.warning
        PlanDayKind.Cardio -> palette.accent
        PlanDayKind.Mobility -> palette.warm
        PlanDayKind.Rest -> palette.textSecondary
    }
}
