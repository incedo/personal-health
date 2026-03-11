package com.incedo.personalhealth.shared

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

actual fun createRootViewController(): Any {
    val controller: UIViewController = ComposeUIViewController {
        PersonalHealthApp()
    }
    return controller
}
