package com.incedo.personalhealth.feature.home

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.Base64

actual fun decodeNutritionPhotoBitmap(dataUrl: String?): ImageBitmap? {
    if (dataUrl.isNullOrBlank()) return null
    val payload = dataUrl.substringAfter("base64,", missingDelimiterValue = "")
    if (payload.isBlank()) return null
    return runCatching {
        val bytes = Base64.getDecoder().decode(payload)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    }.getOrNull()
}
