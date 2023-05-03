package com.akvelon.bitbuckler.domain.prlink

import com.akvelon.bitbuckler.entity.pullrequest.PullRequestParseEntity

interface PRLinkParser {
    suspend fun parseLink(url: String): PullRequestParseEntity
}