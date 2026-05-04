package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
    includeProfile: Boolean,
    onTabSelected: (HomeTab) -> Unit
) {
    val desktopWebStyle = HomeBuildFlags.usesDesktopBottomBarStyle && !compact
    val regularTabs = if (includeProfile) HomeTab.regularTabs else HomeTab.mobileBottomTabs
    val centerGap = when {
        compact -> 88.dp
        desktopWebStyle -> 72.dp
        else -> 116.dp
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        HomeBottomTabsPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = when {
                    compact -> 26.dp
                    desktopWebStyle -> 18.dp
                    else -> 34.dp
                }),
            desktopWebStyle = desktopWebStyle,
            contentPadding = if (desktopWebStyle) 8.dp else 12.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeBottomTabGroup(
                    tabs = regularTabs.take(2),
                    selectedTab = selectedTab,
                    compact = compact,
                    desktopWebStyle = desktopWebStyle,
                    onTabSelected = onTabSelected,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(centerGap))
                HomeBottomTabGroup(
                    tabs = regularTabs.drop(2),
                    selectedTab = selectedTab,
                    compact = compact,
                    desktopWebStyle = desktopWebStyle,
                    onTabSelected = onTabSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        CoachBottomBarButton(
            modifier = Modifier.padding(top = when {
                compact -> 0.dp
                desktopWebStyle -> 2.dp
                else -> 6.dp
            }),
            selected = selectedTab == HomeTab.COACH,
            compact = compact,
            desktopWebStyle = desktopWebStyle,
            onClick = { onTabSelected(HomeTab.COACH) }
        )
    }
}

@Composable
private fun HomeBottomTabGroup(
    tabs: List<HomeTab>,
    selectedTab: HomeTab,
    compact: Boolean,
    desktopWebStyle: Boolean,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        tabs.forEach { tab ->
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
    val background = if (selected) tabAccentSoft(tab) else palette.surface
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick() },
        color = background,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) tabAccent(tab) else palette.surfaceMuted
        )
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = when {
                    compact -> 14.dp
                    desktopWebStyle -> 7.dp
                    else -> 14.dp
                },
                horizontal = if (desktopWebStyle) 10.dp else 12.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                when {
                    compact -> 0.dp
                    desktopWebStyle -> 3.dp
                    else -> 6.dp
                }
            )
        ) {
            HomeTabIcon(
                tab = tab,
                color = if (selected) tabAccent(tab) else palette.textPrimary,
                modifier = Modifier.size(
                    when {
                        compact -> 28.dp
                        desktopWebStyle -> 24.dp
                        else -> 30.dp
                    }
                )
            )
            if (!compact) {
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
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
