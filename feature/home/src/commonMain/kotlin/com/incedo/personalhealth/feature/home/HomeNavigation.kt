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
import androidx.compose.foundation.layout.fillMaxSize
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

internal enum class HomeTab(
    val label: String,
    val navDescription: String,
    val sectionTitle: String,
    val sectionSubtitle: String
) {
    DASHBOARD(
        label = "Vandaag",
        navDescription = "Dagelijkse status en snelle acties",
        sectionTitle = "Vandaag",
        sectionSubtitle = "Je centrale startpunt voor gezondheid en activiteit."
    ),
    NEWS(
        label = "Community",
        navDescription = "Updates, community en inspiratie",
        sectionTitle = "Community",
        sectionSubtitle = "Recente updates, coaching-signalen en communitymomenten."
    ),
    COACH(
        label = "Plan",
        navDescription = "Routines, focus en begeleiding",
        sectionTitle = "Plan",
        sectionSubtitle = "Begeleiding, routines en slimme suggesties op een vaste plek."
    ),
    LOG(
        label = "Loggen",
        navDescription = "Eten, drinken en activiteit",
        sectionTitle = "Loggen",
        sectionSubtitle = "Alles wat je vandaag toevoegt op een vaste plek."
    ),
    PROFILE(
        label = "Profiel",
        navDescription = "Voorkeuren, profiel en privacy",
        sectionTitle = "Profiel",
        sectionSubtitle = "Instellingen, profielkeuzes en privacy."
    );

    companion object {
        val bottomBarTabs: List<HomeTab> = listOf(DASHBOARD, NEWS, COACH, LOG, PROFILE)
        val regularTabs: List<HomeTab> = listOf(DASHBOARD, NEWS, LOG, PROFILE)
    }
}

@Composable
internal fun tabAccent(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accent
    HomeTab.NEWS -> homePalette().warm
    HomeTab.COACH -> homePalette().warning
    HomeTab.LOG -> homePalette().accent
    HomeTab.PROFILE -> homePalette().warning
}

@Composable
internal fun tabAccentSoft(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accentSoft
    HomeTab.NEWS -> homePalette().warmSoft
    HomeTab.COACH -> homePalette().warningSoft
    HomeTab.LOG -> homePalette().accentSoft
    HomeTab.PROFILE -> homePalette().warningSoft
}

@Composable
internal fun HomeSidebar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    HomePanel(
        modifier = modifier.fillMaxSize(),
        contentPadding = 20.dp
    ) {
        Text(
            text = "Personal Health",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = "Eenzelfde navigatiemodel voor home, coach, sync en profiel.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.size(22.dp))
        HomeTab.bottomBarTabs.forEach { tab ->
            val selected = tab == selectedTab
            val background = if (selected) tabAccentSoft(tab) else Color.Transparent
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { onTabSelected(tab) },
                color = background,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) tabAccent(tab) else palette.surfaceMuted
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tab.navDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
internal fun HomeBottomTabs(
    selectedTab: HomeTab,
    compact: Boolean,
    onTabSelected: (HomeTab) -> Unit
) {
    val desktopWebStyle = HomeBuildFlags.usesDesktopBottomBarStyle && !compact
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
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HomeTab.regularTabs.take(2).forEach { tab ->
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
                Spacer(modifier = Modifier.width(centerGap))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HomeTab.regularTabs.drop(2).forEach { tab ->
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
