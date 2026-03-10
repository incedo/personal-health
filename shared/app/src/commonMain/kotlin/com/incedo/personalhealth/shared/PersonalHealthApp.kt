package com.incedo.personalhealth.shared

import androidx.compose.runtime.Composable
import com.incedo.personalhealth.core.designsystem.PersonalHealthTheme
import com.incedo.personalhealth.feature.home.HomeScreen

@Composable
fun PersonalHealthApp() {
    PersonalHealthTheme {
        HomeScreen()
    }
}
