package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PhTagTone { Neutral, Primary, Success, Warning, Danger, Info }

@Composable
fun PhCard(
    modifier: Modifier = Modifier,
    raised: Boolean = false,
    padding: Dp = PhTheme.spacing.xl,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = PhTheme.shapes.lg,
        color = if (raised) PhTheme.colors.surfaceRaised else PhTheme.colors.surface,
        tonalElevation = if (raised) PhTheme.elevation.md else PhTheme.elevation.sm,
        shadowElevation = if (raised) PhTheme.elevation.md else PhTheme.elevation.none,
        border = BorderStroke(1.dp, PhTheme.colors.divider)
    ) {
        Column(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
fun PhTag(
    text: String,
    modifier: Modifier = Modifier,
    tone: PhTagTone = PhTagTone.Neutral,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val palette = tagPalette(tone)
    Surface(
        modifier = modifier,
        shape = PhTheme.shapes.pill,
        color = palette.background,
        contentColor = palette.content
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                CompositionLocalProvider(LocalContentColor provides palette.content, content = it)
            }
            Text(text = text, style = PhTheme.typography.label)
        }
    }
}

@Composable
fun PhSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    action: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.xs)) {
            kicker?.let {
                Text(text = it.uppercase(), style = PhTheme.typography.label, color = PhTheme.colors.textMuted)
            }
            Text(text = title, style = PhTheme.typography.h3, color = PhTheme.colors.text)
        }
        action?.invoke(this)
    }
}

@Composable
fun PhListRow(
    title: String,
    modifier: Modifier = Modifier,
    meta: String? = null,
    value: String? = null,
    sub: String? = null,
    accent: Color = PhTheme.colors.primary,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick == null) {
        modifier
    } else {
        modifier.clickable(role = Role.Button, onClick = onClick)
    }
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(vertical = PhTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading?.let {
            Surface(shape = PhTheme.shapes.md, color = accent.copy(alpha = 0.12f), contentColor = accent) {
                Row(modifier = Modifier.padding(PhTheme.spacing.sm), content = { it() })
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.xs)) {
            Text(text = title, style = PhTheme.typography.body, color = PhTheme.colors.text)
            meta?.let { Text(text = it, style = PhTheme.typography.caption, color = PhTheme.colors.textMuted) }
        }
        value?.let {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = it, style = PhTheme.typography.body, color = PhTheme.colors.text)
                sub?.let { label -> Text(text = label, style = PhTheme.typography.caption, color = PhTheme.colors.textFaint) }
            }
        }
        trailing?.invoke()
    }
}

@Composable
fun PhMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    trend: String? = null,
    tone: PhTagTone = PhTagTone.Primary,
    chart: (@Composable () -> Unit)? = null
) {
    PhCard(modifier = modifier, padding = PhTheme.spacing.lg) {
        Column(verticalArrangement = Arrangement.spacedBy(PhTheme.spacing.sm)) {
            Text(text = label, style = PhTheme.typography.label, color = PhTheme.colors.textMuted)
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.xs)) {
                Text(text = value, style = PhTheme.typography.metric, color = PhTheme.colors.text)
                unit?.let { Text(text = it, style = PhTheme.typography.bodySmall, color = PhTheme.colors.textMuted) }
            }
            trend?.let { PhTag(text = it, tone = tone) }
            chart?.invoke()
        }
    }
}

private data class PhTonePalette(val background: Color, val content: Color)

@Composable
private fun tagPalette(tone: PhTagTone): PhTonePalette {
    val colors = PhTheme.colors
    return when (tone) {
        PhTagTone.Neutral -> PhTonePalette(colors.surfaceMuted, colors.text)
        PhTagTone.Primary -> PhTonePalette(colors.primarySoft, colors.primary)
        PhTagTone.Success -> PhTonePalette(colors.successSoft, colors.success)
        PhTagTone.Warning -> PhTonePalette(colors.warningSoft, colors.warning)
        PhTagTone.Danger -> PhTonePalette(colors.dangerSoft, colors.danger)
        PhTagTone.Info -> PhTonePalette(colors.infoSoft, colors.info)
    }
}
