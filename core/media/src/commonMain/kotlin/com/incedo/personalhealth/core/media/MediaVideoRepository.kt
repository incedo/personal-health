package com.incedo.personalhealth.core.media

interface MediaVideoRepository {
    fun newsAndSupportVideos(): List<MediaVideoItem>
}

object StubMediaVideoRepository : MediaVideoRepository {
    override fun newsAndSupportVideos(): List<MediaVideoItem> = listOf(
        MediaVideoItem(
            id = "mobility-hips-lower-back",
            title = "Mobility flow voor heupen en onderrug",
            source = "YouTube oefening",
            duration = "8 min",
            category = "Herstel",
            cue = "Ideaal na zitten of na een lower body sessie.",
            content = MediaVideoContent.YouTube(
                videoId = "4BOTvaRaDjI",
                launchUrl = "https://www.youtube.com/watch?v=4BOTvaRaDjI"
            )
        ),
        MediaVideoItem(
            id = "deadlift-setup-brace",
            title = "Deadlift setup en brace stap voor stap",
            source = "YouTube oefening",
            duration = "12 min",
            category = "Kracht",
            cue = "Focust op houding, spanning en veilige uitvoering.",
            content = MediaVideoContent.YouTube(
                videoId = "MBbyAqvTNkU",
                launchUrl = "https://www.youtube.com/watch?v=MBbyAqvTNkU"
            )
        ),
        MediaVideoItem(
            id = "support-native-demo",
            title = "Native supportclip voor uitleg of backend video",
            source = "App of backend video",
            duration = "5 sec",
            category = "Ondersteuning",
            cue = "Voor eigen content die je downloadt of later mee verpakt in de app.",
            content = MediaVideoContent.Native(
                uri = "https://samplelib.com/lib/preview/mp4/sample-5s.mp4"
            )
        )
    )
}
