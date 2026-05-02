package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.designsystem.PhChoiceCard
import com.incedo.personalhealth.core.designsystem.PhSegmentedControl
import com.incedo.personalhealth.core.designsystem.PhSegmentedOption
import com.incedo.personalhealth.core.designsystem.PhTextField
import com.incedo.personalhealth.core.designsystem.PhToggle
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun ProfilePanel(profile: OnboardingProfile, onEvent: (OnboardingEvent) -> Unit, modifier: Modifier) {
    InputPanel("Over jou", "Voor accurate kcal-, kracht- en VO2max-berekeningen.", modifier) {
        PhSegmentedControl(
            options = listOf(
                PhSegmentedOption(OnboardingGender.Female.name, "Vrouw"),
                PhSegmentedOption(OnboardingGender.Male.name, "Man"),
                PhSegmentedOption(OnboardingGender.Other.name, "Anders")
            ),
            selectedValue = profile.gender?.name.orEmpty(),
            onValueSelected = { onEvent(OnboardingEvent.ProfileChanged(profile.copy(gender = OnboardingGender.valueOf(it)))) },
            modifier = Modifier.fillMaxWidth()
        )
        ProfileField("Leeftijd", profile.ageYears, "34", "jaar") {
            onEvent(OnboardingEvent.ProfileChanged(profile.copy(ageYears = it)))
        }
        ProfileField("Lengte", profile.heightCm, "178", "cm") {
            onEvent(OnboardingEvent.ProfileChanged(profile.copy(heightCm = it)))
        }
        ProfileField("Gewicht", profile.weightKg, "74.5", "kg") {
            onEvent(OnboardingEvent.ProfileChanged(profile.copy(weightKg = it)))
        }
    }
}

@Composable
internal fun ActivityPanel(selected: OnboardingActivityLevel?, onEvent: (OnboardingEvent) -> Unit, modifier: Modifier) {
    val options = listOf(
        OnboardingActivityLevel.Starter to ("Beginner" to "Net begonnen of na lange pauze."),
        OnboardingActivityLevel.Recreational to ("Recreatief" to "1-2 keer per week sport, basisconditie."),
        OnboardingActivityLevel.Regular to ("Regelmatig" to "3-4 keer per week, gericht aan het trainen."),
        OnboardingActivityLevel.Athletic to ("Atletisch" to "5+ keer per week, prestatiegericht.")
    )
    InputPanel("Hoe actief ben je?", "Eerlijk antwoorden: we starten je op het juiste niveau.", modifier) {
        options.forEach { (level, copy) ->
            PhChoiceCard(
                selected = selected == level,
                onClick = { onEvent(OnboardingEvent.ActivityLevelSelected(level)) },
                title = copy.first,
                description = copy.second
            )
        }
    }
}

@Composable
internal fun AvailabilityPanel(
    availability: OnboardingAvailability,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier
) {
    InputPanel("Wanneer kun je?", "We bouwen je weekplan rond deze beschikbaarheid.", modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.xs)) {
            OnboardingDay.entries.forEach { day ->
                PhChoiceCard(
                    selected = day in availability.days,
                    onClick = { onEvent(OnboardingEvent.AvailabilityChanged(availability.copy(days = availability.days.toggle(day)))) },
                    title = day.shortLabel(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        PhTextField(
            value = availability.weeklyHours.toString(),
            onValueChange = { onEvent(OnboardingEvent.AvailabilityChanged(availability.copy(weeklyHours = it.toIntOrNull() ?: 1))) },
            label = "Uren per week",
            trailingContent = { Text("uur", style = PhTheme.typography.caption, color = PhTheme.colors.textMuted) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun DevicesPanel(devices: List<OnboardingDevice>, onEvent: (OnboardingEvent) -> Unit, modifier: Modifier) {
    InputPanel("Welke data wil je koppelen?", "Koppelen kan nu of later; manual blijft beschikbaar.", modifier) {
        OnboardingDevice.entries.forEach { device ->
            PhToggle(
                checked = device in devices,
                onCheckedChange = { onEvent(OnboardingEvent.DeviceToggled(device)) },
                label = device.label()
            )
        }
    }
}
