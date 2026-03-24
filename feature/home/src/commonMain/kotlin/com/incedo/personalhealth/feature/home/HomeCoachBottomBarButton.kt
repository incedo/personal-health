package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun CoachBottomBarButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    compact: Boolean,
    desktopWebStyle: Boolean = false,
    onClick: () -> Unit
) {
    val palette = homePalette()
    val accent = tabAccent(HomeTab.COACH)
    val outerSize = when {
        compact -> 78.dp
        desktopWebStyle -> 58.dp
        else -> 98.dp
    }
    val innerSize = when {
        compact -> 66.dp
        desktopWebStyle -> 48.dp
        else -> 84.dp
    }
    val iconSize = when {
        compact -> 30.dp
        desktopWebStyle -> 24.dp
        else -> 36.dp
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(outerSize)
                .clip(CircleShape)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) { onClick() },
            shape = CircleShape,
            color = tabAccentSoft(HomeTab.COACH),
            shadowElevation = if (selected) 14.dp else 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(innerSize),
                    shape = CircleShape,
                    color = accent,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 3.dp else 2.dp,
                        color = palette.surface
                    ),
                    shadowElevation = if (selected) 16.dp else 10.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        HomeTabIcon(
                            tab = HomeTab.COACH,
                            color = palette.buttonContent,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }
        }
        if (!compact) {
            Spacer(modifier = Modifier.size(if (desktopWebStyle) 3.dp else 6.dp))
            Text(
                text = HomeTab.COACH.label,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) accent else palette.textPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
