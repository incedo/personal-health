package com.incedo.personalhealth.feature.home

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeNutritionPhotoBitmap(dataUrl: String?): ImageBitmap?
