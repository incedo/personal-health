package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PhButtonVariant { Primary, Secondary, Ghost, Outline, Warning, Danger }
enum class PhButtonSize { Small, Medium, Large }
enum class PhToggleSize { Medium }

data class PhSegmentedOption(
    val value: String,
    val label: String
)

@Composable
fun PhButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PhButtonVariant = PhButtonVariant.Primary,
    size: PhButtonSize = PhButtonSize.Medium,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val colors = PhTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val palette = buttonPalette(variant)
    val background = if (hovered && enabled) palette.hoverBackground else palette.background
    Surface(
        modifier = modifier
            .heightIn(min = size.minHeight)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        shape = PhTheme.shapes.pill,
        color = background.copy(alpha = if (enabled) 1f else 0.38f),
        border = palette.border?.let { BorderStroke(1.dp, it) },
        contentColor = palette.content.copy(alpha = if (enabled) 1f else 0.38f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(size.contentPadding),
            horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.sm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let { PhContentIcon(contentColor = palette.content, content = it) }
            Text(text = text, style = PhTheme.typography.button)
            trailingIcon?.let { PhContentIcon(contentColor = palette.content, content = it) }
        }
    }
}

@Composable
fun PhIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PhButtonVariant = PhButtonVariant.Ghost,
    enabled: Boolean = true,
    size: Dp = 40.dp,
    content: @Composable () -> Unit
) {
    val palette = buttonPalette(variant)
    Surface(
        modifier = modifier
            .size(size)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = CircleShape,
        color = palette.background.copy(alpha = if (enabled) 1f else 0.38f),
        border = palette.border?.let { BorderStroke(1.dp, it) },
        contentColor = palette.content.copy(alpha = if (enabled) 1f else 0.38f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            PhContentIcon(contentColor = palette.content, content = content)
        }
    }
}

@Composable
fun PhTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = PhTheme.typography.body,
        label = label?.let { { Text(it, style = PhTheme.typography.label) } },
        placeholder = placeholder?.let { { Text(it, style = PhTheme.typography.bodySmall) } },
        supportingText = supportingText?.let { { Text(it, style = PhTheme.typography.caption) } },
        trailingIcon = trailingContent,
        isError = isError,
        shape = PhTheme.shapes.md,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = PhTheme.colors.surface,
            unfocusedContainerColor = PhTheme.colors.surface,
            disabledContainerColor = PhTheme.colors.surfaceMuted,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedTextColor = PhTheme.colors.text,
            unfocusedTextColor = PhTheme.colors.text,
            focusedLabelColor = PhTheme.colors.primary,
            unfocusedLabelColor = PhTheme.colors.textMuted
        )
    )
}

@Composable
fun PhToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
) {
    Row(
        modifier = modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onCheckedChange
        ),
        horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 42.dp, height = 24.dp)
                .background(if (checked) PhTheme.colors.primary else PhTheme.colors.surfaceSunken, PhTheme.shapes.pill)
                .padding(3.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(Color.White, CircleShape)
            )
        }
        label?.let { Text(text = it, style = PhTheme.typography.bodySmall, color = PhTheme.colors.text) }
    }
}

@Composable
fun PhSegmentedControl(
    options: List<PhSegmentedOption>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier,
        shape = PhTheme.shapes.pill,
        color = PhTheme.colors.surfaceMuted
    ) {
        Row(
            modifier = Modifier.padding(PhTheme.spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(PhTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                val selected = option.value == selectedValue
                Surface(
                    modifier = Modifier.clickable(
                        enabled = enabled,
                        role = Role.Tab,
                        onClick = { onValueSelected(option.value) }
                    ),
                    shape = PhTheme.shapes.pill,
                    color = if (selected) PhTheme.colors.surface else Color.Transparent,
                    tonalElevation = if (selected) PhTheme.elevation.sm else PhTheme.elevation.none,
                    contentColor = if (selected) PhTheme.colors.text else PhTheme.colors.textMuted
                ) {
                    Text(
                        text = option.label,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        style = PhTheme.typography.label
                    )
                }
            }
        }
    }
}

private data class PhButtonPalette(
    val background: Color,
    val hoverBackground: Color,
    val content: Color,
    val border: Color?
)

@Composable
private fun buttonPalette(variant: PhButtonVariant): PhButtonPalette {
    val colors = PhTheme.colors
    return when (variant) {
        PhButtonVariant.Primary -> PhButtonPalette(colors.primary, colors.primaryHover, colors.onPrimary, null)
        PhButtonVariant.Secondary -> PhButtonPalette(colors.surfaceMuted, colors.surfaceSunken, colors.text, null)
        PhButtonVariant.Ghost -> PhButtonPalette(Color.Transparent, colors.surfaceMuted, colors.text, null)
        PhButtonVariant.Outline -> PhButtonPalette(Color.Transparent, colors.surfaceMuted, colors.text, colors.border)
        PhButtonVariant.Warning -> PhButtonPalette(colors.warning, colors.warning, colors.text, null)
        PhButtonVariant.Danger -> PhButtonPalette(colors.danger, colors.danger, colors.onPrimary, null)
    }
}

private val PhButtonSize.contentPadding
    @Composable get() = when (this) {
        PhButtonSize.Small -> PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        PhButtonSize.Medium -> PaddingValues(horizontal = 18.dp, vertical = 12.dp)
        PhButtonSize.Large -> PaddingValues(horizontal = 22.dp, vertical = 16.dp)
    }

private val PhButtonSize.minHeight: Dp
    get() = when (this) {
        PhButtonSize.Small -> 36.dp
        PhButtonSize.Medium -> 44.dp
        PhButtonSize.Large -> 52.dp
    }

@Composable
private fun PhContentIcon(
    contentColor: Color,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
}
