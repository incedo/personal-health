package com.incedo.personalhealth.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class PhTypography(
    val display: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
    val button: TextStyle,
    val metric: TextStyle
)

val DefaultPhTypography = PhTypography(
    display = TextStyle(
        fontSize = 40.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.SemiBold
    ),
    h1 = TextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.SemiBold
    ),
    h2 = TextStyle(
        fontSize = 22.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.SemiBold
    ),
    h3 = TextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold
    ),
    body = TextStyle(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    label = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    caption = TextStyle(
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    button = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    metric = TextStyle(
        fontSize = 44.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.SemiBold
    )
)

fun PhTypography.toMaterialTypography(): Typography = Typography().copy(
    displayMedium = display,
    headlineMedium = h1,
    headlineSmall = h2,
    titleLarge = h3,
    titleMedium = h3,
    titleSmall = label,
    bodyLarge = body,
    bodyMedium = bodySmall,
    bodySmall = caption,
    labelLarge = button,
    labelMedium = label,
    labelSmall = caption
)
