package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

internal enum class HomeTab(
    val label: String,
    val navDescription: String,
    val sectionTitle: String,
    val sectionSubtitle: String
) {
    DASHBOARD(
        label = "Today",
        navDescription = "Dagelijkse status en snelle acties",
        sectionTitle = "Today",
        sectionSubtitle = "Je centrale startpunt voor gezondheid en activiteit."
    ),
    COACH(
        label = "Plan",
        navDescription = "Routines, focus en begeleiding",
        sectionTitle = "Plan",
        sectionSubtitle = "Begeleiding, routines en slimme suggesties op een vaste plek."
    ),
    LOG(
        label = "Track",
        navDescription = "Eten, drinken en activiteit",
        sectionTitle = "Track",
        sectionSubtitle = "Alles wat je vandaag toevoegt op een vaste plek."
    ),
    PROGRESS(
        label = "Progress",
        navDescription = "Trends, metrics en voortgang",
        sectionTitle = "Progress",
        sectionSubtitle = "Je trenddata, metingen en voortgang bij elkaar."
    ),
    NEWS(
        label = "Community",
        navDescription = "Updates, community en inspiratie",
        sectionTitle = "Community",
        sectionSubtitle = "Recente updates, coaching-signalen en communitymomenten."
    ),
    PROFILE(
        label = "Profile",
        navDescription = "Voorkeuren, profiel en privacy",
        sectionTitle = "Profile",
        sectionSubtitle = "Instellingen, profielkeuzes en privacy."
    );

    companion object {
        val primaryTabs: List<HomeTab> = listOf(DASHBOARD, LOG, COACH, PROGRESS, NEWS)
        val accountTabs: List<HomeTab> = listOf(PROFILE)
        val bottomBarTabs: List<HomeTab> = primaryTabs + accountTabs
    }
}

@Composable
internal fun tabAccent(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accent
    HomeTab.NEWS -> homePalette().warm
    HomeTab.COACH -> homePalette().warning
    HomeTab.LOG -> homePalette().accent
    HomeTab.PROGRESS -> homePalette().accent
    HomeTab.PROFILE -> homePalette().warning
}

@Composable
internal fun tabAccentSoft(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accentSoft
    HomeTab.NEWS -> homePalette().warmSoft
    HomeTab.COACH -> homePalette().warningSoft
    HomeTab.LOG -> homePalette().accentSoft
    HomeTab.PROGRESS -> homePalette().accentSoft
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
