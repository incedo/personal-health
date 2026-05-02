package com.incedo.personalhealth.feature.onboarding

internal fun <T> List<T>.toggle(value: T): List<T> {
    return if (contains(value)) filterNot { it == value } else this + value
}

internal fun OnboardingDay.shortLabel(): String {
    return when (this) {
        OnboardingDay.Monday -> "M"
        OnboardingDay.Tuesday -> "D"
        OnboardingDay.Wednesday -> "W"
        OnboardingDay.Thursday -> "D"
        OnboardingDay.Friday -> "V"
        OnboardingDay.Saturday -> "Z"
        OnboardingDay.Sunday -> "Z"
    }
}

internal fun OnboardingDevice.label(): String {
    return when (this) {
        OnboardingDevice.AppleHealth -> "Apple Health"
        OnboardingDevice.HealthConnect -> "Health Connect"
        OnboardingDevice.Wearable -> "Wearable"
        OnboardingDevice.Manual -> "Handmatig starten"
    }
}

internal fun OnboardingNutritionStyle.label(): String {
    return when (this) {
        OnboardingNutritionStyle.Balanced -> "Gebalanceerd"
        OnboardingNutritionStyle.HighProtein -> "Eiwitrijk"
        OnboardingNutritionStyle.PlantForward -> "Plant-forward"
        OnboardingNutritionStyle.Flexible -> "Flexibel"
    }
}

internal fun OnboardingDietaryRestriction.label(): String {
    return when (this) {
        OnboardingDietaryRestriction.Vegetarian -> "Vegetarisch"
        OnboardingDietaryRestriction.Vegan -> "Vegan"
        OnboardingDietaryRestriction.GlutenFree -> "Glutenvrij"
        OnboardingDietaryRestriction.DairyFree -> "Zuivelvrij"
        OnboardingDietaryRestriction.NutFree -> "Notenvrij"
    }
}
