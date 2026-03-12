package com.incedo.personalhealth.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.incedo.personalhealth.shared.PersonalHealthApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("Personal Health") {
        PersonalHealthApp()
    }
}
