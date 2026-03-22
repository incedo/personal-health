package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.media.MediaVideoContent

@Composable
internal actual fun PlatformEmbeddedVideoPlayer(
    content: MediaVideoContent,
    modifier: Modifier
) {
    val openVideo = rememberPlatformVideoLauncher()
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inline video volgt hier later op iPhone.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = { openVideo(content.launchUrl) }) {
            Text("Open video")
        }
    }
}
