package com.incedo.personalhealth.feature.home

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

actual object HomeBuildFlags {
    @OptIn(ExperimentalNativeApi::class)
    actual val isDebugEditorEnabled: Boolean
        get() = Platform.isDebugBinary
    actual val usesDesktopBottomBarStyle: Boolean = false
}
