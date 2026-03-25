package com.incedo.personalhealth.core.newssocial

data class NewsSocialFeed(
    val heroTitle: String,
    val heroSubtitle: String,
    val statusLabel: String,
    val statusValue: String,
    val highlights: List<NewsSocialHighlight>,
    val videoPosts: List<NewsSocialVideoPost>,
    val source: NewsSocialSource
)

data class NewsSocialHighlight(
    val id: String,
    val title: String,
    val summary: String,
    val metadata: String,
    val author: NewsSocialAuthor,
    val imageUrl: String,
    val accent: NewsSocialAccent
)

data class NewsSocialVideoPost(
    val id: String,
    val category: String,
    val sourceLabel: String,
    val title: String,
    val description: String,
    val cue: String,
    val duration: String,
    val engagementLabel: String,
    val author: NewsSocialAuthor,
    val imageUrl: String,
    val video: NewsSocialVideoLink
)

data class NewsSocialAuthor(
    val name: String,
    val handle: String,
    val role: String
)

sealed interface NewsSocialVideoLink {
    val launchUrl: String
    val previewImageUrl: String

    data class YouTube(
        val videoId: String,
        override val launchUrl: String,
        override val previewImageUrl: String = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    ) : NewsSocialVideoLink

    data class Hosted(
        val uri: String,
        override val previewImageUrl: String,
        override val launchUrl: String = uri
    ) : NewsSocialVideoLink
}

enum class NewsSocialAccent {
    ACCENT,
    WARM,
    WARNING
}

enum class NewsSocialSource {
    STUB,
    BACKEND
}
