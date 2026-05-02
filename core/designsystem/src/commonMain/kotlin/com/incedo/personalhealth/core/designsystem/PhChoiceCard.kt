package com.incedo.personalhealth.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun PhChoiceCard(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    accent: Color = PhTheme.colors.primary,
    selectedBackground: Color = PhTheme.colors.primarySoft,
    leading: (@Composable () -> Unit)? = null,
) {
    val colors = PhTheme.colors
    val spacing = PhTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = when {
        selected -> selectedBackground
        hovered -> colors.surfaceMuted
        else -> colors.surface
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null
            )
            .focusable(interactionSource = interactionSource),
        shape = PhTheme.shapes.xl,
        color = background,
        border = BorderStroke(1.dp, if (selected) accent else colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            leading?.invoke()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Text(text = title, style = PhTheme.typography.h3, color = colors.text)
                description?.let {
                    Text(text = it, style = PhTheme.typography.bodySmall, color = colors.textMuted)
                }
            }
            PhSelectionDot(selected = selected, accent = accent)
        }
    }
}

@Composable
private fun PhSelectionDot(
    selected: Boolean,
    accent: Color
) {
    val colors = PhTheme.colors
    Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = if (selected) accent else Color.Transparent,
        border = BorderStroke(2.dp, if (selected) accent else colors.borderStrong)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                )
            }
        }
    }
}
