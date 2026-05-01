package com.incedo.personalhealth.core.newssocial

interface NewsSocialFeedApi {
    suspend fun getFeed(request: NewsSocialFeedRequest): NewsSocialFeed
}

data class NewsSocialFeedRequest(
    val profileName: String
)
