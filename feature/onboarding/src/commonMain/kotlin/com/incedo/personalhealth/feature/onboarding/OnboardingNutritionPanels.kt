package com.incedo.personalhealth.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.designsystem.PhCard
import com.incedo.personalhealth.core.designsystem.PhChoiceCard
import com.incedo.personalhealth.core.designsystem.PhTextField
import com.incedo.personalhealth.core.designsystem.PhToggle
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun NutritionPanel(nutrition: OnboardingNutrition, onEvent: (OnboardingEvent) -> Unit, modifier: Modifier) {
    InputPanel("Hoe eet je meestal?", "Voeding wordt praktischer als we je voorkeuren kennen.", modifier) {
        OnboardingNutritionStyle.entries.forEach { style ->
            PhChoiceCard(
                selected = nutrition.style == style,
                onClick = { onEvent(OnboardingEvent.NutritionChanged(nutrition.copy(style = style))) },
                title = style.label()
            )
        }
        OnboardingDietaryRestriction.entries.forEach { restriction ->
            PhToggle(
                checked = restriction in nutrition.restrictions,
                onCheckedChange = {
                    onEvent(
                        OnboardingEvent.NutritionChanged(
                            nutrition.copy(restrictions = nutrition.restrictions.toggle(restriction))
                        )
                    )
                },
                label = restriction.label()
            )
        }
    }
}

@Composable
internal fun BaselinePanel(baseline: OnboardingBaseline, onEvent: (OnboardingEvent) -> Unit, modifier: Modifier) {
    InputPanel("Je startpunt", "Een baseline maakt vooruitgang zichtbaar zonder ruis.", modifier) {
        ProfileField("Slaap", baseline.sleepHours, "7.5", "uur") {
            onEvent(OnboardingEvent.BaselineChanged(baseline.copy(sleepHours = it)))
        }
        ProfileField("Rusthartslag", baseline.restingHeartRateBpm, "58", "bpm") {
            onEvent(OnboardingEvent.BaselineChanged(baseline.copy(restingHeartRateBpm = it)))
        }
        ProfileField("Gewicht", baseline.bodyWeightKg, "74.5", "kg") {
            onEvent(OnboardingEvent.BaselineChanged(baseline.copy(bodyWeightKg = it)))
        }
    }
}

@Composable
internal fun InputPanel(title: String, description: String, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    PhCard(modifier = modifier, padding = PhTheme.spacing.xl) {
        Column(verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.md)) {
            Text(text = title, style = PhTheme.typography.h2, color = PhTheme.colors.text)
            Text(text = description, style = PhTheme.typography.bodySmall, color = PhTheme.colors.textMuted)
            content()
        }
    }
}

@Composable
internal fun ProfileField(label: String, value: String, placeholder: String, unit: String, onChange: (String) -> Unit) {
    PhTextField(
        value = value,
        onValueChange = { onChange(it.filter { char -> char.isDigit() || char == '.' }.take(5)) },
        label = label,
        placeholder = placeholder,
        trailingContent = { Text(unit, style = PhTheme.typography.caption, color = PhTheme.colors.textMuted) },
        modifier = Modifier.fillMaxWidth()
    )
}
