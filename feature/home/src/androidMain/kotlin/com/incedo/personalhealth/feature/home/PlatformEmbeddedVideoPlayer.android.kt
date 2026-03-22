package com.incedo.personalhealth.feature.home

import android.graphics.Color as AndroidColor
import android.net.Uri
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.incedo.personalhealth.core.media.MediaVideoContent

@Composable
internal actual fun PlatformEmbeddedVideoPlayer(
    content: MediaVideoContent,
    modifier: Modifier
) {
    when (content) {
        is MediaVideoContent.Native -> NativeVideoPlayer(
            uri = content.uri,
            modifier = modifier
        )
        is MediaVideoContent.YouTube -> UnsupportedYoutubeInlinePlayer(
            url = content.launchUrl,
            modifier = modifier
        )
    }
}

@Composable
private fun NativeVideoPlayer(
    uri: String,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(AndroidColor.BLACK)
                setMediaController(MediaController(context).also { controller ->
                    controller.setAnchorView(this)
                })
            }
        },
        update = { view ->
            if (view.tag != uri) {
                view.tag = uri
                view.setVideoURI(Uri.parse(uri))
                view.setOnPreparedListener { player ->
                    player.isLooping = false
                    view.start()
                }
            }
        }
    )
}

@Composable
private fun UnsupportedYoutubeInlinePlayer(
    url: String,
    modifier: Modifier
) {
    val openVideo = rememberPlatformVideoLauncher()
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "YouTube opent hier via de YouTube-app.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = { openVideo(url) }) {
            Text("Open in YouTube")
        }
    }
}
