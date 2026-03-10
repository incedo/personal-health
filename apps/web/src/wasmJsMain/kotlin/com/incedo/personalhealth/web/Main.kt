package com.incedo.personalhealth.web

import androidx.compose.ui.window.CanvasBasedWindow
import com.incedo.personalhealth.shared.PersonalHealthApp

fun main() {
    CanvasBasedWindow("Personal Health") {
        PersonalHealthApp()
    }
}
