package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhAvatarVariant

@Composable
internal fun HomeNavigationHeader(
    selectedTab: HomeTab,
    avatarVariant: PhAvatarVariant,
    onTabSelected: (HomeTab) -> Unit
) {
    HomeTopTabs(
        selectedTab = selectedTab,
        avatarVariant = avatarVariant,
        onTabSelected = onTabSelected
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
internal fun HomeProfileShortcutOverlay(
    selectedTab: HomeTab,
    avatarVariant: PhAvatarVariant,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 14.dp, end = 14.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeMobileProfileShortcut(
            selected = selectedTab == HomeTab.PROFILE,
            avatarVariant = avatarVariant,
            onClick = { onTabSelected(HomeTab.PROFILE) }
        )
    }
}

@Composable
internal fun HomeNavigationFooter(
    selectedTab: HomeTab,
    compact: Boolean,
    expanded: Boolean,
    onTabSelected: (HomeTab) -> Unit
) {
    if (!expanded) {
        Spacer(modifier = Modifier.height(12.dp))
        HomeBottomTabs(
            selectedTab = selectedTab,
            compact = compact,
            onTabSelected = onTabSelected
        )
    }
}
