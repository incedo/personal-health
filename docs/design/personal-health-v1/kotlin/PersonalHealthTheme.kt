package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Personal Health design tokens — calm, longevity-focused.
 * Single source of truth, mirrored from `tokens.css` in the design-system project.
 *
 * Use `PhTheme.colors`, `PhTheme.typography`, `PhTheme.spacing`, `PhTheme.shapes`,
 * `PhTheme.elevation`, `PhTheme.motion` instead of hard-coded values. Hard-coded
 * dp/sp/hex inside feature/UI is forbidden by repo rules.
 */

// ─────────── Colors ───────────

@Immutable
data class PhColors(
    val primary: Color,
    val primaryHover: Color,
    val primaryPressed: Color,
    val primarySoft: Color,
    val onPrimary: Color,

    val accent: Color,
    val accentSoft: Color,

    val background: Color,
    val backgroundSoft: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceMuted: Color,
    val surfaceSunken: Color,

    val divider: Color,
    val border: Color,
    val borderStrong: Color,

    val text: Color,
    val textMuted: Color,
    val textFaint: Color,
    val onPrimaryText: Color,

    val success: Color, val successSoft: Color,
    val warning: Color, val warningSoft: Color,
    val danger: Color,  val dangerSoft: Color,
    val info: Color,    val infoSoft: Color,

    val data: List<Color>,   // 7-step viz scale
    val zones: List<Color>,  // 5 HR/load zones
    val isDark: Boolean
)

val PhLightColors = PhColors(
    primary = Color(0xFF2F6F4E),
    primaryHover = Color(0xFF275E42),
    primaryPressed = Color(0xFF1F4F37),
    primarySoft = Color(0xFFE2EEE7),
    onPrimary = Color(0xFFFFFFFF),

    accent = Color(0xFF7AB89A),
    accentSoft = Color(0xFFDBEDE2),

    background = Color(0xFFF4F7F5),
    backgroundSoft = Color(0xFFECF1ED),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFFFFFFF),
    surfaceMuted = Color(0xFFEEF2EF),
    surfaceSunken = Color(0xFFE4EAE6),

    divider = Color(0xFFDDE4DF),
    border = Color(0xFFCFD8D2),
    borderStrong = Color(0xFF1F2A24),

    text = Color(0xFF1F2A24),
    textMuted = Color(0xFF5A6B62),
    textFaint = Color(0xFF8A9991),
    onPrimaryText = Color(0xFFFFFFFF),

    success = Color(0xFF2F6F4E), successSoft = Color(0xFFE2EEE7),
    warning = Color(0xFFC7873A), warningSoft = Color(0xFFF6EBD9),
    danger = Color(0xFFB14A4A),  dangerSoft = Color(0xFFF4DEDE),
    info = Color(0xFF3A6F8A),    infoSoft = Color(0xFFE0EBF1),

    data = listOf(
        Color(0xFF2F6F4E), Color(0xFF7AB89A), Color(0xFFC7873A),
        Color(0xFF3A6F8A), Color(0xFF8E6FA8), Color(0xFFB14A4A), Color(0xFF4F6B5C)
    ),
    zones = listOf(
        Color(0xFFB5CFC0), Color(0xFF7AB89A), Color(0xFF4E9C77),
        Color(0xFFC7873A), Color(0xFFB14A4A)
    ),
    isDark = false
)

val PhDarkColors = PhColors(
    primary = Color(0xFF7AB89A),
    primaryHover = Color(0xFF8DC4A8),
    primaryPressed = Color(0xFF69A88A),
    primarySoft = Color(0xFF1A3329),
    onPrimary = Color(0xFF0E1F17),

    accent = Color(0xFFA6D6BA),
    accentSoft = Color(0xFF1F3B2D),

    background = Color(0xFF0E1714),
    backgroundSoft = Color(0xFF131E1A),
    surface = Color(0xFF16221E),
    surfaceRaised = Color(0xFF1B2A24),
    surfaceMuted = Color(0xFF1F302A),
    surfaceSunken = Color(0xFF0B1411),

    divider = Color(0xFF233129),
    border = Color(0xFF2C3D34),
    borderStrong = Color(0xFFE6EDE8),

    text = Color(0xFFE6EDE8),
    textMuted = Color(0xFF9AAAA0),
    textFaint = Color(0xFF6E7E74),
    onPrimaryText = Color(0xFF0E1F17),

    success = Color(0xFF7AB89A), successSoft = Color(0xFF1F3B2D),
    warning = Color(0xFFE1A75A), warningSoft = Color(0xFF3A2C12),
    danger = Color(0xFFE27A7A),  dangerSoft = Color(0xFF3A1E1E),
    info = Color(0xFF7DA9C0),    infoSoft = Color(0xFF1B2C36),

    data = listOf(
        Color(0xFF7AB89A), Color(0xFFA6D6BA), Color(0xFFE1A75A),
        Color(0xFF7DA9C0), Color(0xFFB8A0D0), Color(0xFFE27A7A), Color(0xFF99B0A4)
    ),
    zones = listOf(
        Color(0xFF2C3D34), Color(0xFF4F8467), Color(0xFF7AB89A),
        Color(0xFFE1A75A), Color(0xFFE27A7A)
    ),
    isDark = true
)

// ─────────── Typography (Inter, geometric sans) ───────────

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
    display = TextStyle(fontSize = 40.sp, lineHeight = 48.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp),
    h1 = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.25).sp),
    h2 = TextStyle(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
    h3 = TextStyle(fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
    body = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal),
    label = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
    caption = TextStyle(fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Normal),
    button = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    metric = TextStyle(fontSize = 44.sp, lineHeight = 48.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.75).sp)
)

// ─────────── Spacing (4pt grid) ───────────

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
    val giant: Dp = 56.dp
)

val DefaultPhSpacing = PhSpacing()

// ─────────── Shapes ───────────

@Immutable
data class PhShapes(
    val xs: Shape = RoundedCornerShape(4.dp),
    val sm: Shape = RoundedCornerShape(8.dp),
    val md: Shape = RoundedCornerShape(12.dp),
    val lg: Shape = RoundedCornerShape(16.dp),
    val xl: Shape = RoundedCornerShape(24.dp),
    val pill: Shape = RoundedCornerShape(50)
)

val DefaultPhShapes = PhShapes()

// ─────────── Elevation ───────────

@Immutable
data class PhElevation(
    val none: Dp = 0.dp,
    val sm: Dp = 1.dp,
    val md: Dp = 2.dp,
    val lg: Dp = 6.dp,
    val xl: Dp = 16.dp
)

val DefaultPhElevation = PhElevation()

// ─────────── Motion ───────────

@Immutable
data class PhMotion(
    val fastMs: Int = 140,
    val normalMs: Int = 240,
    val slowMs: Int = 420
)

val DefaultPhMotion = PhMotion()

// ─────────── CompositionLocals ───────────

val LocalPhColors = staticCompositionLocalOf { PhLightColors }
val LocalPhTypography = staticCompositionLocalOf { DefaultPhTypography }
val LocalPhSpacing = staticCompositionLocalOf { DefaultPhSpacing }
val LocalPhShapes = staticCompositionLocalOf { DefaultPhShapes }
val LocalPhElevation = staticCompositionLocalOf { DefaultPhElevation }
val LocalPhMotion = staticCompositionLocalOf { DefaultPhMotion }

object PhTheme {
    val colors: PhColors
        @Composable @ReadOnlyComposable get() = LocalPhColors.current
    val typography: PhTypography
        @Composable @ReadOnlyComposable get() = LocalPhTypography.current
    val spacing: PhSpacing
        @Composable @ReadOnlyComposable get() = LocalPhSpacing.current
    val shapes: PhShapes
        @Composable @ReadOnlyComposable get() = LocalPhShapes.current
    val elevation: PhElevation
        @Composable @ReadOnlyComposable get() = LocalPhElevation.current
    val motion: PhMotion
        @Composable @ReadOnlyComposable get() = LocalPhMotion.current
}

@Composable
fun PersonalHealthTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val ph = if (darkTheme) PhDarkColors else PhLightColors
    val m3Scheme = if (darkTheme) {
        darkColorScheme(
            primary = ph.primary, onPrimary = ph.onPrimary,
            background = ph.background, onBackground = ph.text,
            surface = ph.surface, onSurface = ph.text,
            surfaceVariant = ph.surfaceMuted, onSurfaceVariant = ph.textMuted,
            error = ph.danger, onError = ph.onPrimary, outline = ph.border
        )
    } else {
        lightColorScheme(
            primary = ph.primary, onPrimary = ph.onPrimary,
            background = ph.background, onBackground = ph.text,
            surface = ph.surface, onSurface = ph.text,
            surfaceVariant = ph.surfaceMuted, onSurfaceVariant = ph.textMuted,
            error = ph.danger, onError = ph.onPrimary, outline = ph.border
        )
    }

    val m3Typography = Typography()
        .copy(
            displayMedium = DefaultPhTypography.display,
            headlineMedium = DefaultPhTypography.h1,
            headlineSmall = DefaultPhTypography.h2,
            titleLarge = DefaultPhTypography.h3,
            bodyLarge = DefaultPhTypography.body,
            bodyMedium = DefaultPhTypography.bodySmall,
            labelLarge = DefaultPhTypography.button,
            labelMedium = DefaultPhTypography.label,
            labelSmall = DefaultPhTypography.caption
        )

    CompositionLocalProvider(
        LocalPhColors provides ph,
        LocalPhTypography provides DefaultPhTypography,
        LocalPhSpacing provides DefaultPhSpacing,
        LocalPhShapes provides DefaultPhShapes,
        LocalPhElevation provides DefaultPhElevation,
        LocalPhMotion provides DefaultPhMotion
    ) {
        MaterialTheme(
            colorScheme = m3Scheme,
            typography = m3Typography,
            content = content
        )
    }
}
