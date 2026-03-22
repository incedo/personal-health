package com.incedo.personalhealth.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.media.MediaVideoContent

@Composable
internal expect fun PlatformEmbeddedVideoPlayer(
    content: MediaVideoContent,
    modifier: Modifier = Modifier
)
