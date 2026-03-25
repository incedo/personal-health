package com.incedo.personalhealth.core.newssocial

class StubNewsSocialFeedApi : NewsSocialFeedApi {
    private var callCount: Int = 0

    override suspend fun getFeed(request: NewsSocialFeedRequest): NewsSocialFeed {
        return stubNewsSocialFeed(request, callCount++)
    }
}

fun defaultNewsSocialFeed(request: NewsSocialFeedRequest): NewsSocialFeed = stubNewsSocialFeed(
    request = request,
    rotation = 0
)

private val heroTitles = listOf(
    "Wat speelt er vandaag",
    "Nieuws dat beweegt",
    "Social en training nu"
)

private fun heroSubtitles(profileName: String) = listOf(
    "Een lichte feed voor $profileName met communitymomenten, video-updates en health-content uit een stub API.",
    "Vandaag rouleert de feed met posts, beelden en video-links zodat de nieuws-tab al backend-ready wordt opgebouwd.",
    "Deze feed combineert inspiratie, social proof en coachbare video’s in data die later direct uit backend en client kan komen."
)

private val highlights = listOf(
    NewsSocialHighlight(
        id = "community-run-vondelpark",
        title = "Community run in Vondelpark",
        summary = "Een vroege groep start zaterdag om 08:30 voor een rustige 5 km met koffiestop na afloop.",
        metadata = "24 mensen geïnteresseerd",
        author = NewsSocialAuthor("Mila Janssen", "@mila.moves", "Community host"),
        imageUrl = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1200&q=80",
        accent = NewsSocialAccent.ACCENT
    ),
    NewsSocialHighlight(
        id = "mobility-reset-desk-athletes",
        title = "Mobility reset voor bureau-atleten",
        summary = "Een korte flow van 8 minuten blijft populair bij lopers en lifters die veel zitten overdag.",
        metadata = "Past goed na krachttraining",
        author = NewsSocialAuthor("Noor de Vries", "@noor.recovers", "Recovery coach"),
        imageUrl = "https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1200&q=80",
        accent = NewsSocialAccent.WARNING
    ),
    NewsSocialHighlight(
        id = "protein-breakfast-checkin",
        title = "Eiwitrijk ontbijt blijft winnen",
        summary = "Steeds meer gebruikers delen ontbijtcombinaties met yoghurt, fruit en extra eiwitten voor stabielere energie.",
        metadata = "Bekijk wat vandaag werkt",
        author = NewsSocialAuthor("Jurre Bakker", "@jurre.fuels", "Nutrition editor"),
        imageUrl = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=1200&q=80",
        accent = NewsSocialAccent.WARM
    )
)

private val videoPosts = listOf(
    NewsSocialVideoPost(
        id = "mobility-hips-lower-back",
        category = "Herstel",
        sourceLabel = "Nieuwsfeed video",
        title = "Mobility flow voor heupen en onderrug",
        description = "Een korte sessie die vaak wordt gedeeld na lange zitdagen en lower-body training.",
        cue = "Ideaal na zitten of na een lower body sessie.",
        duration = "8 min",
        engagementLabel = "Veel opgeslagen door runners",
        author = NewsSocialAuthor("Sara Kuiper", "@sara.moves", "Movement coach"),
        imageUrl = "https://img.youtube.com/vi/4BOTvaRaDjI/hqdefault.jpg",
        video = NewsSocialVideoLink.YouTube(
            videoId = "4BOTvaRaDjI",
            launchUrl = "https://www.youtube.com/watch?v=4BOTvaRaDjI"
        )
    ),
    NewsSocialVideoPost(
        id = "deadlift-setup-brace",
        category = "Kracht",
        sourceLabel = "Coach uitleg",
        title = "Deadlift setup en brace stap voor stap",
        description = "Een technische walkthrough die gebruikers delen wanneer ze hun pulling mechanics willen opschonen.",
        cue = "Focust op houding, spanning en veilige uitvoering.",
        duration = "12 min",
        engagementLabel = "Trending in strength",
        author = NewsSocialAuthor("Daan Post", "@daan.lifts", "Strength coach"),
        imageUrl = "https://img.youtube.com/vi/MBbyAqvTNkU/hqdefault.jpg",
        video = NewsSocialVideoLink.YouTube(
            videoId = "MBbyAqvTNkU",
            launchUrl = "https://www.youtube.com/watch?v=MBbyAqvTNkU"
        )
    ),
    NewsSocialVideoPost(
        id = "hosted-breath-reset",
        category = "Focus",
        sourceLabel = "App video",
        title = "Breathing reset voor tussen meetings",
        description = "Een korte hosted clip voor gebruikers die even uit stress en schermfocus willen stappen.",
        cue = "Gebruik deze tussen werkblokken of voor je avondwandeling.",
        duration = "45 sec",
        engagementLabel = "Nieuw in de feed",
        author = NewsSocialAuthor("Lena Vos", "@lena.balance", "Breathwork coach"),
        imageUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1200&q=80",
        video = NewsSocialVideoLink.Hosted(
            uri = "https://samplelib.com/lib/preview/mp4/sample-5s.mp4",
            previewImageUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1200&q=80"
        )
    )
)

private fun stubNewsSocialFeed(
    request: NewsSocialFeedRequest,
    rotation: Int
): NewsSocialFeed {
    val subtitles = heroSubtitles(request.profileName)
    return NewsSocialFeed(
        heroTitle = heroTitles[rotation.mod(heroTitles.size)],
        heroSubtitle = subtitles[rotation.mod(subtitles.size)],
        statusLabel = "Live",
        statusValue = "${videoPosts.size} updates",
        highlights = rotate(highlights, rotation),
        videoPosts = rotate(videoPosts, rotation),
        source = NewsSocialSource.STUB
    )
}

private fun <T> rotate(items: List<T>, amount: Int): List<T> {
    if (items.isEmpty()) return items
    val pivot = amount.mod(items.size)
    return items.drop(pivot) + items.take(pivot)
}
