package com.akvelon.bitbuckler.model.repository.local

import com.akvelon.bitbuckler.model.entity.CachedRepoData
import java.time.LocalDateTime

interface CachedReposRepository {

    suspend fun getReposBy(workspaceSlug: String, projectKey: String): List<CachedRepoData>

    suspend fun getTrackedRepos(): List<CachedRepoData>

    suspend fun getRepo(uuid: String): CachedRepoData?

    suspend fun updateTrackCounter()

    suspend fun lastUpdateDataBy(workspaceSlug: String): LocalDateTime?

    suspend fun replaceAllBy(workspaceSlug: String, projectKey: String, repos: List<CachedRepoData>)

    suspend fun getTrackedCounter(): Int

    suspend fun updateTrackedFlag(uuid: String, isTracked: Boolean)

    suspend fun clear()
}