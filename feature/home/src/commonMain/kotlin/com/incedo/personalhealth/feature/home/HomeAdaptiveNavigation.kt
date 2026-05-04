package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeNavigationHeader(
    selectedTab: HomeTab,
    compact: Boolean,
    expanded: Boolean,
    onTabSelected: (HomeTab) -> Unit
) {
    if (expanded) {
        HomeTopTabs(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
        Spacer(modifier = Modifier.height(12.dp))
    } else if (compact) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeMobileProfileShortcut(
                selected = selectedTab == HomeTab.PROFILE,
                onClick = { onTabSelected(HomeTab.PROFILE) }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
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
            includeProfile = !compact,
            onTabSelected = onTabSelected
        )
    }
}
