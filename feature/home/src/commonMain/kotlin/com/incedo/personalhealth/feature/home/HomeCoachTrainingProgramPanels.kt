package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhSectionHeader
import com.incedo.personalhealth.core.designsystem.PhTag
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.goals.CoachProtocol

@Composable
internal fun MesocyclePanel(selectedProtocol: CoachProtocol) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(kicker = "Mesocycle", title = selectedProtocol.title) {
            PhTag("Fase: opbouw", tone = PhTagTone.Primary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)) {
            (1..5).forEach { week ->
                val current = week == 3
                val deload = week == 5
                Surface(
                    modifier = Modifier.weight(1f),
                    color = when {
                        current -> palette.warning
                        week < 3 -> palette.warningSoft
                        deload -> palette.surfaceMuted
                        else -> palette.surface
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (deload) palette.warning else Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "W$week",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (current) palette.buttonContent else palette.textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            text = if (deload) "↓" else "${50 + week * 10}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (current) palette.buttonContent else palette.textPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
        Text(
            text = "Volume bouwt op tot week 4, daarna deload. Coach stuurt bij op herstel en compliance.",
            style = MaterialTheme.typography.bodySmall,
            color = palette.textSecondary
        )
    }
}

@Composable
internal fun FeelTodayPanel() {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(kicker = "Vandaag", title = "Hoe voel je je?")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 12.dp)) {
            (1..5).forEach { score ->
                val selected = score == 4
                Surface(
                    modifier = Modifier.weight(1f),
                    color = if (selected) homePalette().warning else homePalette().surfaceMuted,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = score.toString(),
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) homePalette().buttonContent else homePalette().textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Text(
            text = "Score 4/5: plan blijft staan. Bij 1-2 schalen we automatisch terug.",
            style = MaterialTheme.typography.bodySmall,
            color = homePalette().textSecondary,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
internal fun VolumeTargetPanel(selectedProtocol: CoachProtocol) {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(kicker = "Target volume", title = "Sets per focus")
        volumeTargets(selectedProtocol).forEach { target ->
            VolumeTargetRow(target = target)
        }
    }
}

@Composable
private fun VolumeTargetRow(target: VolumeTarget) {
    val palette = homePalette()
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(target.name, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
            Text("${target.current} / ${target.max}", style = MaterialTheme.typography.labelMedium, color = palette.textSecondary)
        }
        LinearProgressIndicator(
            progress = { target.current / target.max.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp).clip(RoundedCornerShape(999.dp)),
            color = target.color,
            trackColor = palette.surfaceMuted
        )
    }
}

@Composable
internal fun WeekLoadBar(current: Int, target: Int) {
    val palette = homePalette()
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text("Belasting deze week", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
        Text("$current / $target TSS", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
    }
    LinearProgressIndicator(
        progress = { current / target.toFloat() },
        modifier = Modifier.fillMaxWidth().height(10.dp).padding(top = 6.dp).clip(RoundedCornerShape(999.dp)),
        color = palette.warning,
        trackColor = palette.surfaceMuted
    )
}
