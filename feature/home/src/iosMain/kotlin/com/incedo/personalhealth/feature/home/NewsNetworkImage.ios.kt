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
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
    fetchImageBytes(imageUrl).decodeToImageBitmap()
}

@OptIn(ExperimentalForeignApi::class)
private suspend fun fetchImageBytes(imageUrl: String): ByteArray = suspendCancellableCoroutine { continuation ->
    val url = NSURL.URLWithString(imageUrl)
    if (url == null) {
        continuation.resumeWithException(IllegalArgumentException("Invalid image url: $imageUrl"))
        return@suspendCancellableCoroutine
    }

    val task = NSURLSession.sharedSession.dataTaskWithURL(url) { data, _, error ->
        when {
            data != null -> continuation.resume(data.toByteArray())
            error != null -> continuation.resumeWithException(
                IllegalStateException(error.localizedDescription ?: "Unable to load image: $imageUrl")
            )

            else -> continuation.resumeWithException(IllegalStateException("Unable to load image: $imageUrl"))
        }
    }

    continuation.invokeOnCancellation { task.cancel() }
    task.resume()
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    return ByteArray(size).apply {
        usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    }
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
