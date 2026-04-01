package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.Image
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
import kotlinx.coroutines.await
import kotlin.js.JsArray
import kotlin.js.JsAny
import kotlin.js.JsNumber
import kotlin.js.Promise
import org.jetbrains.compose.resources.decodeToImageBitmap

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
            modifier = modifier
                .fillMaxWidth()
                .height(height),
            contentScale = ContentScale.Crop
        )

        failed -> Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .background(homePalette().surface.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "!",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }

        else -> Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .background(homePalette().surface.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = homePalette().buttonContent)
        }
    }
}

@OptIn(org.jetbrains.compose.resources.ExperimentalResourceApi::class)
private suspend fun loadRemoteImageBitmap(imageUrl: String): ImageBitmap {
    val bytes = fetchImageByteArray(imageUrl)
    return bytes.decodeToImageBitmap()
}

@JsFun("(url) => fetch(url).then(r => r.arrayBuffer()).then(b => Array.from(new Uint8Array(b)))")
private external fun fetchImageBytes(url: String): Promise<JsAny?>

private suspend fun fetchImageByteArray(url: String): ByteArray {
    val jsArray = fetchImageBytes(url).await<JsArray<JsNumber?>>()
    return ByteArray(jsArray.length) { index -> jsArray[index]!!.toInt().toByte() }
}
