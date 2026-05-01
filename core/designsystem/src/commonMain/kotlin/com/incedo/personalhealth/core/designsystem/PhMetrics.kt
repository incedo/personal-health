package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PhSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val huge: Dp = 40.dp,
    val giant: Dp = 56.dp,
    val massive: Dp = 72.dp
)

val DefaultPhSpacing = PhSpacing()

@Immutable
data class PhShapes(
    val xs: CornerBasedShape = RoundedCornerShape(4.dp),
    val sm: CornerBasedShape = RoundedCornerShape(8.dp),
    val md: CornerBasedShape = RoundedCornerShape(12.dp),
    val lg: CornerBasedShape = RoundedCornerShape(16.dp),
    val xl: CornerBasedShape = RoundedCornerShape(24.dp),
    val pill: CornerBasedShape = RoundedCornerShape(999.dp)
)

val DefaultPhShapes = PhShapes()

fun PhShapes.toMaterialShapes(): Shapes = Shapes(
    extraSmall = xs,
    small = sm,
    medium = md,
    large = lg,
    extraLarge = xl
)

@Immutable
data class PhElevation(
    val none: Dp = 0.dp,
    val sm: Dp = 1.dp,
    val md: Dp = 2.dp,
    val lg: Dp = 6.dp,
    val xl: Dp = 16.dp
)

val DefaultPhElevation = PhElevation()

@Immutable
data class PhMotion(
    val fastMs: Int = 140,
    val normalMs: Int = 240,
    val slowMs: Int = 420
)

val DefaultPhMotion = PhMotion()
