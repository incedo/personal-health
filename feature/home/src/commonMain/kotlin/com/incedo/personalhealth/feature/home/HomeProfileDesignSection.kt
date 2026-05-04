package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhAvatar
import com.incedo.personalhealth.core.designsystem.PhAvatarVariant
import com.incedo.personalhealth.core.designsystem.PhButton
import com.incedo.personalhealth.core.designsystem.PhButtonVariant
import com.incedo.personalhealth.core.designsystem.PhSectionHeader
import com.incedo.personalhealth.core.designsystem.PhTag
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun ProfileDesignContent(
    profileName: String,
    fitScore: Int,
    themeMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    fitnessBodyProfile: FitnessBodyProfile,
    onFitnessBodyProfileSelected: (FitnessBodyProfile) -> Unit,
    onOpenDevTest: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        ProfileIdentityCard(
            profileName = profileName,
            fitScore = fitScore,
            avatarVariant = fitnessBodyProfile.toAvatarVariant()
        )
        BoxWithConstraints {
            val compact = maxWidth < 840.dp
            if (compact) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    ProfileMainColumn()
                    ProfileSettingsColumn(themeMode, onThemeModeSelected, fitnessBodyProfile, onFitnessBodyProfileSelected, onOpenDevTest)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1.45f)) { ProfileMainColumn() }
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileSettingsColumn(themeMode, onThemeModeSelected, fitnessBodyProfile, onFitnessBodyProfileSelected, onOpenDevTest)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileIdentityCard(
    profileName: String,
    fitScore: Int,
    avatarVariant: PhAvatarVariant
) {
    val colors = PhTheme.colors
    HomePanel(modifier = Modifier.fillMaxWidth(), contentPadding = 24.dp) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            PhAvatar(
                variant = avatarVariant,
                size = 88.dp
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Profiel", style = PhTheme.typography.label, color = colors.textMuted)
                Text(profileName, style = PhTheme.typography.h1, color = colors.text, fontWeight = FontWeight.SemiBold)
                Text("kees@example.nl · Lid sinds maart 2024", style = PhTheme.typography.bodySmall, color = colors.textMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PhTag("14 dagen streak", tone = PhTagTone.Primary)
                    PhTag("Fit score $fitScore", tone = PhTagTone.Success)
                }
            }
        }
    }
}

@Composable
private fun ProfileMainColumn() {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        VitalsCard()
        DevicesCard()
        GoalsCard()
    }
}

@Composable
private fun ProfileSettingsColumn(
    themeMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    fitnessBodyProfile: FitnessBodyProfile,
    onFitnessBodyProfileSelected: (FitnessBodyProfile) -> Unit,
    onOpenDevTest: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        ActivityAvailabilityCard()
        PrivacyCard()
        PreferencesCard(themeMode, onThemeModeSelected, fitnessBodyProfile, onFitnessBodyProfileSelected)
        DataExportCard(onOpenDevTest)
    }
}

@Composable
private fun VitalsCard() {
    val vitals = listOf(
        Triple("Leeftijd", "34", "jaar"),
        Triple("Lengte", "178", "cm"),
        Triple("Gewicht", "74.5", "kg"),
        Triple("Rust HR", "52", "bpm"),
        Triple("HRV", "68", "ms"),
        Triple("VO2max", "47.2", "ml/kg/min")
    )
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(title = "Jij", kicker = "Basisstats")
        Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            vitals.chunked(3).forEach { column ->
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    column.forEach { (label, value, unit) -> StatTile(label, value, unit) }
                }
            }
        }
    }
}

@Composable
private fun DevicesCard() {
    val devices = listOf(
        "Apple Watch Series 9" to "HR · HRV · slaap · 2 min",
        "Withings Body+" to "Gewicht · vetpercentage · vanmorgen",
        "Oura Ring Gen3" to "Niet verbonden"
    )
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(title = "Apparaten", kicker = "Bronnen")
        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            devices.forEachIndexed { index, item ->
                ProfileRow(title = item.first, detail = item.second, selected = index < 2)
            }
        }
    }
}

@Composable
private fun GoalsCard() {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(title = "Doelen", kicker = "Plan richting")
        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileRow("Langer gezond leven", "Hoofddoel", selected = true)
            ProfileRow("Kracht opbouwen", "Secundair doel", selected = false)
            ProfileRow("Conditie verbeteren", "Secundair doel", selected = false)
        }
    }
}

@Composable
private fun ActivityAvailabilityCard() {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text("Activiteit", style = PhTheme.typography.label, color = PhTheme.colors.textMuted)
        Text("Regelmatig", style = PhTheme.typography.h3, color = PhTheme.colors.text, modifier = Modifier.padding(top = 4.dp))
        Text("3-4x per week, gericht trainen", style = PhTheme.typography.bodySmall, color = PhTheme.colors.textMuted)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("M", "D", "W", "D", "V", "Z", "Z").forEachIndexed { index, day ->
                val active = index in listOf(0, 1, 3, 4, 5)
                StatDay(day = day, active = active, modifier = Modifier.weight(1f))
            }
        }
        Text("5 dagen/week · 6 uur/week", style = PhTheme.typography.caption, color = PhTheme.colors.textMuted, modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
private fun PrivacyCard() {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(title = "Privacy")
        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileRow("Trainingen", "Vrienden", selected = true)
            ProfileRow("Voeding", "Alleen ik", selected = false)
            ProfileRow("Gewicht", "Alleen ik", selected = false)
            ProfileRow("Slaap & herstel", "Vrienden", selected = true)
        }
    }
}

@Composable
private fun PreferencesCard(
    themeMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    fitnessBodyProfile: FitnessBodyProfile,
    onFitnessBodyProfileSelected: (FitnessBodyProfile) -> Unit
) {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        PhSectionHeader(title = "Voorkeuren")
        Text("Thema", style = PhTheme.typography.label, color = PhTheme.colors.textMuted, modifier = Modifier.padding(top = 12.dp))
        ChoiceRow(HomeThemeMode.entries, themeMode, { it.label }, onThemeModeSelected)
        Text("Lichaamsprofiel", style = PhTheme.typography.label, color = PhTheme.colors.textMuted, modifier = Modifier.padding(top = 14.dp))
        ChoiceRow(FitnessBodyProfile.entries, fitnessBodyProfile, { if (it == FitnessBodyProfile.MALE) "Man" else "Vrouw" }, onFitnessBodyProfileSelected)
    }
}

@Composable
private fun DataExportCard(onOpenDevTest: () -> Unit) {
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text("Jouw data", style = PhTheme.typography.label, color = PhTheme.colors.textMuted)
        Text("Volledige export in CSV/JSON. Verwijdering binnen 30 dagen.", style = PhTheme.typography.bodySmall, color = PhTheme.colors.textMuted, modifier = Modifier.padding(top = 6.dp))
        PhButton("Download mijn data", onClick = {}, variant = PhButtonVariant.Outline, modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
        PhButton("Dev/Test menu", onClick = onOpenDevTest, variant = PhButtonVariant.Ghost, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
    }
}

@Composable
private fun StatTile(label: String, value: String, unit: String) {
    Surface(color = PhTheme.colors.surfaceMuted, shape = PhTheme.shapes.md) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(label.uppercase(), style = PhTheme.typography.caption, color = PhTheme.colors.textMuted)
            Text("$value $unit", style = PhTheme.typography.h3, color = PhTheme.colors.text, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
private fun ProfileRow(title: String, detail: String, selected: Boolean) {
    val tone = if (selected) PhTheme.colors.primarySoft else PhTheme.colors.surfaceMuted
    Surface(color = tone, shape = RoundedCornerShape(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, style = PhTheme.typography.body, color = PhTheme.colors.text)
            Text(detail, style = PhTheme.typography.caption, color = PhTheme.colors.textMuted)
        }
    }
}

@Composable
private fun StatDay(day: String, active: Boolean, modifier: Modifier) {
    Surface(modifier = modifier, color = if (active) PhTheme.colors.primary else PhTheme.colors.surfaceMuted, shape = PhTheme.shapes.sm) {
        Text(
            day,
            modifier = Modifier.padding(vertical = 8.dp),
            style = PhTheme.typography.label,
            color = if (active) PhTheme.colors.onPrimary else PhTheme.colors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun <T> ChoiceRow(options: List<T>, selected: T, label: (T) -> String, onSelected: (T) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                modifier = Modifier.weight(1f).clip(PhTheme.shapes.pill).clickable { onSelected(option) },
                color = if (isSelected) PhTheme.colors.primarySoft else PhTheme.colors.surfaceMuted,
                shape = PhTheme.shapes.pill
            ) {
                Text(label(option), modifier = Modifier.padding(vertical = 10.dp), style = PhTheme.typography.label, color = PhTheme.colors.text, textAlign = TextAlign.Center)
            }
        }
    }
}
