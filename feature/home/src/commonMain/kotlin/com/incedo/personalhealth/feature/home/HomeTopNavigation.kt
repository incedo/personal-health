package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhAvatar
import com.incedo.personalhealth.core.designsystem.PhAvatarVariant

@Composable
internal fun HomeTopTabs(
    selectedTab: HomeTab,
    avatarVariant: PhAvatarVariant,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = palette.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Personal Health",
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(12.dp))
            HomeTab.primaryTabs.forEach { tab ->
                HomeTopTabButton(
                    tab = tab,
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            HomeMobileProfileShortcut(
                selected = selectedTab == HomeTab.PROFILE,
                avatarVariant = avatarVariant,
                onClick = { onTabSelected(HomeTab.PROFILE) }
            )
        }
    }
}

@Composable
internal fun HomeMobileProfileShortcut(
    selected: Boolean,
    avatarVariant: PhAvatarVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val background = if (selected) tabAccentSoft(HomeTab.PROFILE) else palette.surface
    Surface(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick() },
        shape = CircleShape,
        color = background,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) tabAccent(HomeTab.PROFILE) else palette.surfaceMuted
        ),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            PhAvatar(
                variant = avatarVariant,
                size = 38.dp,
                selected = selected
            )
        }
    }
}

@Composable
private fun HomeTopTabButton(
    tab: HomeTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) tabAccentSoft(tab) else palette.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) tabAccent(tab) else palette.surfaceMuted
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTabIcon(
                tab = tab,
                color = if (selected) tabAccent(tab) else palette.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) palette.textPrimary else palette.textSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}
