package com.akvelon.bitbuckler.domain.repo

import com.akvelon.bitbuckler.model.entity.CachedRepoData
import com.akvelon.bitbuckler.model.entity.repository.Repository

interface CachedReposUseCase {

    suspend fun getReposBy(workspaceSlug: String, projectKey: String): List<CachedRepoData>

    suspend fun updateTrackedFlagForRepos(repositories: List<Repository>)

    suspend fun updateIfNeeded(projectKey: String, workspaceSlug: String): List<CachedRepoData>

    suspend fun forceUpdate(projectKey: String, workspaceSlug: String): List<CachedRepoData>

    suspend fun tryTrackRepository(uuid: String): Boolean

    suspend fun unTrackRepository(uuid: String)

    suspend fun clear()

    suspend fun clearTracked()
}