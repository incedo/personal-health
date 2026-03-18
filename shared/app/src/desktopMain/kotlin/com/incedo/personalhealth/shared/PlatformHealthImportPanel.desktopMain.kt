package com.incedo.personalhealth.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.health.CanonicalHealthImportDocument

@Composable
actual fun PlatformHealthImportPanel(
    onImportDocument: suspend (CanonicalHealthImportDocument) -> Unit,
    onImportMessage: suspend (String) -> Unit,
    modifier: Modifier
) = Unit
