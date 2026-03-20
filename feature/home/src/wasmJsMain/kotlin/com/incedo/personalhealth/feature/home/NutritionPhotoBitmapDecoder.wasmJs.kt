package com.incedo.personalhealth.feature.home

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.skia.Image

@OptIn(ExperimentalEncodingApi::class)
actual fun decodeNutritionPhotoBitmap(dataUrl: String?): ImageBitmap? {
    if (dataUrl.isNullOrBlank()) return null
    val payload = dataUrl.substringAfter("base64,", missingDelimiterValue = "")
    if (payload.isBlank()) return null
    return runCatching {
        Image.makeFromEncoded(Base64.decode(payload)).toComposeImageBitmap()
    }.getOrNull()
}
