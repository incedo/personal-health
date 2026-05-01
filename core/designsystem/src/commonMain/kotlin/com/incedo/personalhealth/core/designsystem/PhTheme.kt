package com.incedo.personalhealth.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPhColors = staticCompositionLocalOf { PhLightColors }
val LocalPhTypography = staticCompositionLocalOf { DefaultPhTypography }
val LocalPhSpacing = staticCompositionLocalOf { DefaultPhSpacing }
val LocalPhShapes = staticCompositionLocalOf { DefaultPhShapes }
val LocalPhElevation = staticCompositionLocalOf { DefaultPhElevation }
val LocalPhMotion = staticCompositionLocalOf { DefaultPhMotion }

object PhTheme {
    val colors: PhColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPhColors.current

    val typography: PhTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalPhTypography.current

    val spacing: PhSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalPhSpacing.current

    val shapes: PhShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalPhShapes.current

    val elevation: PhElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalPhElevation.current

    val motion: PhMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalPhMotion.current
}

@Composable
fun PersonalHealthTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) PhDarkColors else PhLightColors
    val typography = DefaultPhTypography
    val shapes = DefaultPhShapes

    CompositionLocalProvider(
        LocalPhColors provides colors,
        LocalPhTypography provides typography,
        LocalPhSpacing provides DefaultPhSpacing,
        LocalPhShapes provides shapes,
        LocalPhElevation provides DefaultPhElevation,
        LocalPhMotion provides DefaultPhMotion
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography = typography.toMaterialTypography(),
            shapes = shapes.toMaterialShapes(),
            content = content
        )
    }
}

private fun PhColors.toMaterialColorScheme(): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primarySoft,
            onPrimaryContainer = text,
            secondary = accent,
            onSecondary = onPrimary,
            secondaryContainer = accentSoft,
            onSecondaryContainer = text,
            tertiary = info,
            onTertiary = onPrimary,
            tertiaryContainer = infoSoft,
            onTertiaryContainer = text,
            background = background,
            onBackground = text,
            surface = surface,
            onSurface = text,
            surfaceVariant = surfaceMuted,
            onSurfaceVariant = textMuted,
            error = danger,
            onError = onPrimary,
            errorContainer = dangerSoft,
            onErrorContainer = text,
            outline = border,
            outlineVariant = divider
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primarySoft,
            onPrimaryContainer = text,
            secondary = accent,
            onSecondary = onPrimary,
            secondaryContainer = accentSoft,
            onSecondaryContainer = text,
            tertiary = info,
            onTertiary = onPrimary,
            tertiaryContainer = infoSoft,
            onTertiaryContainer = text,
            background = background,
            onBackground = text,
            surface = surface,
            onSurface = text,
            surfaceVariant = surfaceMuted,
            onSurfaceVariant = textMuted,
            error = danger,
            onError = onPrimary,
            errorContainer = dangerSoft,
            onErrorContainer = text,
            outline = border,
            outlineVariant = divider
        )
    }
}
