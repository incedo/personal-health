package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import platform.Foundation.NSURL

@Composable
internal actual fun NewsNetworkImage(
    imageUrl: String,
    contentDescription: String,
    height: Dp,
    modifier: Modifier
) {
    var imageBitmap by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }
    var failed by remember(imageUrl) { mutableStateOf(false) }

    LaunchedEffect(imageUrl) {
        failed = false
        imageBitmap = runCatching { loadRemoteImageBitmap(imageUrl) }.getOrElse {
            failed = true
            null
        }
    }

    when {
        imageBitmap != null -> Image(
            bitmap = imageBitmap!!,
            contentDescription = contentDescription,
            modifier = modifier.fillMaxWidth().height(height),
            contentScale = ContentScale.Crop
        )

        failed -> PlaceholderImageState(modifier, height, "!")
        else -> LoadingImageState(modifier, height)
    }
}

@OptIn(ExperimentalResourceApi::class)
private suspend fun loadRemoteImageBitmap(imageUrl: String): ImageBitmap {
    return fetchImageBytes(imageUrl).decodeToImageBitmap()
}

private suspend fun fetchImageBytes(imageUrl: String): ByteArray {
    NSURL.URLWithString(imageUrl) ?: throw IllegalArgumentException("Invalid image url: $imageUrl")
    throw IllegalStateException("Remote image loading is unavailable on iOS: $imageUrl")
}

@Composable
private fun LoadingImageState(modifier: Modifier, height: Dp) {
    Box(
        modifier = modifier.fillMaxWidth().height(height).background(homePalette().surface.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = homePalette().buttonContent)
    }
}

@Composable
private fun PlaceholderImageState(modifier: Modifier, height: Dp, label: String) {
    Box(
        modifier = modifier.fillMaxWidth().height(height).background(homePalette().surface.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.White, style = MaterialTheme.typography.titleLarge)
    }
}
