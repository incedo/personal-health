package com.incedo.personalhealth.core.newssocial

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class StubNewsSocialFeedApiTest {
    private val api = StubNewsSocialFeedApi()

    @Test
    fun returnsFeedWithHighlightsAndVideos() = runTest {
        val feed = api.getFeed(NewsSocialFeedRequest(profileName = "Kees"))

        assertEquals("Live", feed.statusLabel)
        assertEquals(3, feed.highlights.size)
        assertEquals(3, feed.videoPosts.size)
        assertTrue(feed.highlights.all { it.imageUrl.startsWith("https://") })
        assertTrue(feed.videoPosts.all { it.imageUrl.startsWith("https://") })
        assertTrue(feed.videoPosts.all { it.video.launchUrl.startsWith("https://") })
        assertTrue(feed.videoPosts.all { it.author.name.isNotBlank() })
        assertEquals(NewsSocialSource.STUB, feed.source)
    }

    @Test
    fun rotatesHeroAndItemOrderAcrossCalls() = runTest {
        val request = NewsSocialFeedRequest(profileName = "Kees")

        val first = api.getFeed(request)
        val second = api.getFeed(request)

        assertTrue(first.heroTitle != second.heroTitle || first.heroSubtitle != second.heroSubtitle)
        assertTrue(first.highlights.first().id != second.highlights.first().id)
        assertTrue(first.videoPosts.first().id != second.videoPosts.first().id)
    }
}
