package com.incedo.personalhealth.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.incedo.personalhealth.core.designsystem.PersonalHealthTheme
import com.incedo.personalhealth.feature.home.HomeScreen
import com.incedo.personalhealth.feature.onboarding.OnboardingRoute

@Composable
fun PersonalHealthApp() {
    var onboardingComplete by rememberSaveable { mutableStateOf(false) }

    PersonalHealthTheme {
        if (onboardingComplete) {
            HomeScreen()
        } else {
            OnboardingRoute(onFinished = { onboardingComplete = true })
        }
    }
}
