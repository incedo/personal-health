package com.incedo.personalhealth.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.incedo.personalhealth.shared.PersonalHealthApp

fun main() = application {
    Window(
        title = "Personal Health",
        onCloseRequest = ::exitApplication
    ) {
        PersonalHealthApp()
    }
}
