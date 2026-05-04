package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeBottomTabs(
    selectedTab: HomeTab,
    compact: Boolean,
    onTabSelected: (HomeTab) -> Unit
) {
    val desktopWebStyle = HomeBuildFlags.usesDesktopBottomBarStyle && !compact
    HomeBottomTabsPanel(
        desktopWebStyle = desktopWebStyle,
        contentPadding = if (desktopWebStyle) 8.dp else 10.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.primaryTabs.forEach { tab ->
                HomeBottomTabButton(
                    tab = tab,
                    selected = tab == selectedTab,
                    compact = compact,
                    desktopWebStyle = desktopWebStyle,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HomeBottomTabButton(
    tab: HomeTab,
    selected: Boolean,
    compact: Boolean,
    desktopWebStyle: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val background = if (selected) tabAccentSoft(tab) else Color.Transparent
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick() },
        color = background,
        shape = RoundedCornerShape(16.dp),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(1.dp, tabAccent(tab))
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = when {
                    compact -> 8.dp
                    desktopWebStyle -> 7.dp
                    else -> 10.dp
                },
                horizontal = if (compact) 4.dp else 10.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                when {
                    compact -> 3.dp
                    desktopWebStyle -> 3.dp
                    else -> 4.dp
                }
            )
        ) {
            HomeTabIcon(
                tab = tab,
                color = if (selected) tabAccent(tab) else palette.textPrimary,
                modifier = Modifier.size(
                    when {
                        compact -> 20.dp
                        desktopWebStyle -> 24.dp
                        else -> 24.dp
                    }
                )
            )
            Text(
                text = tab.label,
                style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                color = if (selected) tabAccent(tab) else palette.textSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HomeBottomTabsPanel(
    modifier: Modifier = Modifier,
    desktopWebStyle: Boolean,
    contentPadding: androidx.compose.ui.unit.Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = homePalette()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (desktopWebStyle) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                palette.surface.copy(alpha = 0.72f),
                                palette.surface
                            )
                        )
                    } else {
                        Brush.verticalGradient(listOf(palette.surface, palette.surface))
                    }
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}
