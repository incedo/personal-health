package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class HomePalette(
    val backdropTop: Color,
    val backdropBottom: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceMuted: Color,
    val accent: Color,
    val accentSoft: Color,
    val warm: Color,
    val warmSoft: Color,
    val warning: Color,
    val warningSoft: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val buttonContent: Color
)

private val HomeDarkPalette = HomePalette(
    backdropTop = Color(0xFF071A29),
    backdropBottom = Color(0xFF0B2234),
    surface = Color(0xFF10293D),
    surfaceRaised = Color(0xFF15354B),
    surfaceMuted = Color(0xFF1D425B),
    accent = Color(0xFF19C7B4),
    accentSoft = Color(0xFF124C57),
    warm = Color(0xFFF5C451),
    warmSoft = Color(0xFF4A3A13),
    warning = Color(0xFFFF6F7D),
    warningSoft = Color(0xFF4D2130),
    textPrimary = Color(0xFFF5F7FA),
    textSecondary = Color(0xFFA6B5C4),
    buttonContent = Color(0xFF05262A)
)

private val HomeLightPalette = HomePalette(
    backdropTop = Color(0xFFF5FBFF),
    backdropBottom = Color(0xFFE6F2F7),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFF3F8FB),
    surfaceMuted = Color(0xFFD4E2EA),
    accent = Color(0xFF0FA897),
    accentSoft = Color(0xFFD9F5F1),
    warm = Color(0xFFE0A800),
    warmSoft = Color(0xFFF8EDC8),
    warning = Color(0xFFE26376),
    warningSoft = Color(0xFFFADCE1),
    textPrimary = Color(0xFF10293D),
    textSecondary = Color(0xFF5F7488),
    buttonContent = Color(0xFFFFFFFF)
)

@Composable
private fun homePalette(): HomePalette = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
    HomeDarkPalette
} else {
    HomeLightPalette
}

@Composable
private fun tabAccent(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accent
    HomeTab.SYNC -> homePalette().warm
    HomeTab.PROFILE -> homePalette().warning
}

@Composable
private fun tabAccentSoft(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accentSoft
    HomeTab.SYNC -> homePalette().warmSoft
    HomeTab.PROFILE -> homePalette().warningSoft
}

@Composable
fun HomeScreen(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    profileName: String,
    themeMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    syncContent: @Composable ColumnScope.() -> Unit,
    profileContent: @Composable ColumnScope.() -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.DASHBOARD) }
    val palette = homePalette()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(palette.backdropTop, palette.backdropBottom)))
    ) {
        val compact = maxWidth < 720.dp
        val expanded = maxWidth >= 1080.dp
        val outerPadding = if (compact) 16.dp else 24.dp
        val contentSpacing = if (compact) 14.dp else 20.dp

        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = outerPadding, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    HomeTabContent(
                        selectedTab = selectedTab,
                        fitScore = fitScore,
                        steps = steps,
                        stepsTimeline = stepsTimeline,
                        heartRateBpm = heartRateBpm,
                        profileName = profileName,
                        themeMode = themeMode,
                        onThemeModeSelected = onThemeModeSelected,
                        activityOptions = activityOptions,
                        activityEntries = activityEntries,
                        onLogActivity = onLogActivity,
                        syncContent = syncContent,
                        profileContent = profileContent,
                        compact = true
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                HomeBottomTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(outerPadding),
                horizontalArrangement = Arrangement.spacedBy(contentSpacing)
            ) {
                HomeSidebar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.width(if (expanded) 220.dp else 184.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    HomeTabContent(
                        selectedTab = selectedTab,
                        fitScore = fitScore,
                        steps = steps,
                        stepsTimeline = stepsTimeline,
                        heartRateBpm = heartRateBpm,
                        profileName = profileName,
                        themeMode = themeMode,
                        onThemeModeSelected = onThemeModeSelected,
                        activityOptions = activityOptions,
                        activityEntries = activityEntries,
                        onLogActivity = onLogActivity,
                        syncContent = syncContent,
                        profileContent = profileContent,
                        compact = false
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTabContent(
    selectedTab: HomeTab,
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    profileName: String,
    themeMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit,
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    syncContent: @Composable ColumnScope.() -> Unit,
    profileContent: @Composable ColumnScope.() -> Unit,
    compact: Boolean
) {
    when (selectedTab) {
        HomeTab.DASHBOARD -> DashboardContent(
            fitScore = fitScore,
            steps = steps,
            stepsTimeline = stepsTimeline,
            heartRateBpm = heartRateBpm,
            profileName = profileName,
            activityOptions = activityOptions,
            activityEntries = activityEntries,
            onLogActivity = onLogActivity,
            compact = compact
        )

        HomeTab.SYNC -> HomeSectionScreen(
            tab = HomeTab.SYNC,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Sync overzicht",
                    title = "Integraties en imports",
                    subtitle = "Bekijk je laatste syncstatus en stuur handmatige imports aan wanneer dat nodig is.",
                    accent = homePalette().warm,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Data sources",
                            value = "Actief"
                        )
                    }
                )
            },
            bodyContent = syncContent
        )

        HomeTab.PROFILE -> HomeSectionScreen(
            tab = HomeTab.PROFILE,
            compact = compact,
            leadingContent = {
                HomeHeroCard(
                    eyebrow = "Profiel",
                    title = "Jouw basis en voorkeuren",
                    subtitle = "Houd accountinstellingen, units en persoonlijke status op een vaste plek bij elkaar.",
                    accent = homePalette().accent,
                    compact = compact,
                    sideContent = {
                        HomeStatusBadge(
                            label = "Fit score",
                            value = "$fitScore%"
                        )
                    }
                )
            },
            bodyContent = {
                ThemeModeCard(
                    selectedMode = themeMode,
                    onThemeModeSelected = onThemeModeSelected
                )
                Spacer(modifier = Modifier.height(18.dp))
                ProfileFitScoreCard(
                    fitScore = fitScore,
                    profileName = profileName,
                    modifier = Modifier.fillMaxWidth()
                )
                profileContent()
            }
        )
    }
}

@Composable
private fun DashboardContent(
    fitScore: Int,
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    heartRateBpm: Int,
    profileName: String,
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    compact: Boolean
) {
    var isStepsGraphVisible by rememberSaveable { mutableStateOf(!compact) }
    val palette = homePalette()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val expanded = maxWidth >= 960.dp
        val spacing = if (compact) 14.dp else 18.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            HomeHeroCard(
                eyebrow = "Today",
                title = "Welkom terug, $profileName",
                subtitle = "Je dagstart in een duidelijk overzicht met herstel, activiteit en de eerstvolgende actie.",
                accent = palette.accent,
                compact = compact,
                sideContent = {
                    ProfileRing(
                        fitScore = fitScore,
                        profileName = profileName,
                        modifier = Modifier.size(if (compact) 176.dp else 208.dp)
                    )
                }
            )

            if (expanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        SummaryStrip(
                            steps = steps,
                            heartRateBpm = heartRateBpm,
                            fitScore = fitScore
                        )
                        StepsOverviewCard(
                            steps = steps,
                            stepsTimeline = stepsTimeline,
                            expanded = isStepsGraphVisible,
                            onToggle = { isStepsGraphVisible = !isStepsGraphVisible }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        QuickActivityLogCard(
                            activityOptions = activityOptions,
                            activityEntries = activityEntries,
                            onLogActivity = onLogActivity,
                            modifier = Modifier.fillMaxWidth()
                        )
                        GuidanceCard(
                            fitScore = fitScore,
                            heartRateBpm = heartRateBpm
                        )
                    }
                }
            } else {
                SummaryStrip(
                    steps = steps,
                    heartRateBpm = heartRateBpm,
                    fitScore = fitScore
                )
                StepsOverviewCard(
                    steps = steps,
                    stepsTimeline = stepsTimeline,
                    expanded = isStepsGraphVisible,
                    onToggle = { isStepsGraphVisible = !isStepsGraphVisible }
                )
                QuickActivityLogCard(
                    activityOptions = activityOptions,
                    activityEntries = activityEntries,
                    onLogActivity = onLogActivity,
                    modifier = Modifier.fillMaxWidth()
                )
                GuidanceCard(
                    fitScore = fitScore,
                    heartRateBpm = heartRateBpm
                )
            }
        }
    }
}

@Composable
private fun HomeSectionScreen(
    tab: HomeTab,
    compact: Boolean,
    leadingContent: @Composable ColumnScope.() -> Unit,
    bodyContent: @Composable ColumnScope.() -> Unit
) {
    val palette = homePalette()
    val spacing = if (compact) 14.dp else 18.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        leadingContent()
        HomePanel(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = tab.sectionTitle,
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tab.sectionSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(18.dp))
            bodyContent()
        }
    }
}

@Composable
private fun HomeSidebar(
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
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Eenzelfde navigatiemodel voor home, sync en profiel.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(22.dp))
        HomeTab.entries.forEach { tab ->
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
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HomeBottomTabs(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    val palette = homePalette()
    HomePanel(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 10.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeTab.entries.forEach { tab ->
                val selected = tab == selectedTab
                val background = if (selected) tabAccentSoft(tab) else palette.surface
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { onTabSelected(tab) },
                    color = background,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) tabAccent(tab) else palette.surfaceMuted
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCard(
    eyebrow: String,
    title: String,
    subtitle: String,
    accent: Color,
    compact: Boolean,
    sideContent: @Composable () -> Unit
) {
    val palette = homePalette()
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        val heroPadding = if (compact) 20.dp else 28.dp
        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.verticalGradient(listOf(accent.copy(alpha = 0.18f), palette.surface)))
                    .padding(heroPadding),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                HomeHeroCopy(
                    eyebrow = eyebrow,
                    title = title,
                    subtitle = subtitle
                )
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    sideContent()
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(accent.copy(alpha = 0.2f), palette.surface)
                        )
                    )
                    .padding(heroPadding),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeHeroCopy(
                        eyebrow = eyebrow,
                        title = title,
                        subtitle = subtitle
                    )
                }
                sideContent()
            }
        }
    }
}

@Composable
private fun HomeHeroCopy(
    eyebrow: String,
    title: String,
    subtitle: String
) {
    val palette = homePalette()
    Text(
        text = eyebrow.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = palette.textSecondary
    )
    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall,
        color = palette.textPrimary,
        fontWeight = FontWeight.SemiBold
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = palette.textSecondary
    )
}

@Composable
private fun SummaryStrip(
    steps: Int,
    heartRateBpm: Int,
    fitScore: Int
) {
    val palette = homePalette()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryMetricTile(
            title = "Stappen",
            value = formatSteps(steps),
            subtitle = "Vandaag",
            accent = palette.accent,
            modifier = Modifier.weight(1f)
        )
        SummaryMetricTile(
            title = "Hartslag",
            value = "$heartRateBpm bpm",
            subtitle = "Rustgemiddelde",
            accent = palette.warning,
            modifier = Modifier.weight(1f)
        )
        SummaryMetricTile(
            title = "Score",
            value = "$fitScore/100",
            subtitle = "Dagstatus",
            accent = palette.warm,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryMetricTile(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
        }
    }
}

@Composable
private fun StepsOverviewCard(
    steps: Int,
    stepsTimeline: List<StepTimelinePoint>,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val palette = homePalette()
    HomePanel(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Stappenoverzicht",
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Doel: 10.000 stappen. Tik om ${if (expanded) "het detail te verbergen" else "het detail te tonen"}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
            HomeStatusBadge(
                label = "Totaal",
                value = formatSteps(steps)
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(18.dp))
            StepsTodayGraphCard(
                stepsTimeline = stepsTimeline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GuidanceCard(
    fitScore: Int,
    heartRateBpm: Int
) {
    val palette = homePalette()
    val guidance = when {
        fitScore >= 80 -> "Sterke dag. Houd je ritme vast en plan alleen lichte extra belasting."
        heartRateBpm >= 75 -> "Hartslag ligt hoger dan ideaal. Kies vandaag voor herstel of een korte sessie."
        else -> "Je basis is stabiel. Goed moment voor een geplande training of een stevige wandeling."
    }

    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Focus voor vandaag",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = guidance,
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
    }
}

@Composable
private fun ThemeModeCard(
    selectedMode: HomeThemeMode,
    onThemeModeSelected: (HomeThemeMode) -> Unit
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Thema",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Schakel de app tussen systeemstand, dark en light zonder van scherm te wisselen.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeThemeMode.entries.forEach { mode ->
                val selected = mode == selectedMode
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onThemeModeSelected(mode) },
                    color = if (selected) palette.accentSoft else palette.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) palette.accent else palette.surfaceMuted
                    )
                ) {
                    Text(
                        text = mode.label,
                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textPrimary,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActivityLogCard(
    activityOptions: List<QuickActivityType>,
    activityEntries: List<QuickActivityEntry>,
    onLogActivity: (QuickActivityType) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    var selectedType by rememberSaveable { mutableStateOf(activityOptions.firstOrNull() ?: QuickActivityType.RUNNING) }

    HomePanel(modifier = modifier) {
        Text(
            text = "Snelle activiteit",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = quickActivitySummary(activityEntries),
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        activityOptions.chunked(3).forEach { activityRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                activityRow.forEach { option ->
                    val selected = option == selectedType
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { selectedType = option },
                        color = if (selected) palette.accentSoft else palette.surface,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) palette.accent else palette.surfaceMuted
                        )
                    ) {
                        Text(
                            text = option.label,
                            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(
            onClick = { onLogActivity(selectedType) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.buttonContent
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Log ${selectedType.label.lowercase()}",
                modifier = Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
        if (activityEntries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            activityEntries.take(4).forEachIndexed { index, entry ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (index == 0) palette.surfaceRaised else palette.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
                ) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.textPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                    )
                }
                if (index < activityEntries.take(4).lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun StepsTodayGraphCard(
    stepsTimeline: List<StepTimelinePoint>,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val maxValue = (stepsTimeline.maxOfOrNull { it.steps } ?: 0).coerceAtLeast(1)
    Surface(
        modifier = modifier,
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stappen vandaag",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Totaal ${formatSteps(stepsTimeline.sumOf { it.steps })}",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(184.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                stepsTimeline.forEach { point ->
                    val ratio = (point.steps.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = point.steps.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((96.dp * ratio).coerceAtLeast(8.dp))
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(palette.accent, palette.accentSoft)
                                    )
                                )
                        )
                        Text(
                            text = point.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileFitScoreCard(
    fitScore: Int,
    profileName: String,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    HomePanel(modifier = modifier) {
        Text(
            text = "Dagstatus",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Je persoonlijke score en basisstatus op een vaste plek.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ProfileRing(
                fitScore = fitScore,
                profileName = profileName,
                modifier = Modifier.size(220.dp)
            )
        }
    }
}

@Composable
private fun ProfileRing(
    fitScore: Int,
    profileName: String,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val clamped = fitScore.coerceIn(0, 100)
    val progress = clamped / 100f
    val initials = profileName.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifBlank { "PH" }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 18.dp.toPx()
            drawArc(
                color = palette.surfaceMuted,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = palette.accent,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(palette.accentSoft)
                    .border(width = 2.dp, color = palette.accent, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary
                )
            }
            Text(
                text = "$clamped%",
                style = MaterialTheme.typography.headlineSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Fit score",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }
    }
}

@Composable
private fun HomeStatusBadge(
    label: String,
    value: String
) {
    val palette = homePalette()
    Surface(
        color = palette.accentSoft,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.accent)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HomePanel(
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.ui.unit.Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = homePalette()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content
        )
    }
}

private enum class HomeTab(
    val label: String,
    val navDescription: String,
    val sectionTitle: String,
    val sectionSubtitle: String
) {
    DASHBOARD(
        label = "Home",
        navDescription = "Dagelijkse status en snelle acties",
        sectionTitle = "Vandaag",
        sectionSubtitle = "Je centrale startpunt voor gezondheid en activiteit."
    ),
    SYNC(
        label = "Sync",
        navDescription = "Brondatastatus en imports",
        sectionTitle = "Sync",
        sectionSubtitle = "Bronnen, kanaalstatus en importacties op een vaste plek."
    ),
    PROFILE(
        label = "Profiel",
        navDescription = "Persoonlijke gegevens en voorkeuren",
        sectionTitle = "Profiel",
        sectionSubtitle = "Instellingen, status en accountinformatie."
    )
}
