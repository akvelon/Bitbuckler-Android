package com.akvelon.bitbuckler.domain.repo

import android.content.SharedPreferences
import com.akvelon.bitbuckler.model.datasource.local.LocalFlags
import com.akvelon.bitbuckler.model.entity.CachedRepoData
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.model.entity.toCached
import com.akvelon.bitbuckler.model.repository.*
import com.akvelon.bitbuckler.model.repository.local.CachedReposRepository
import com.akvelon.bitbuckler.model.repository.pullrequest.TrackedIssuesRepo
import com.akvelon.bitbuckler.model.repository.pullrequest.TrackedPullRequestsRepo
import java.time.LocalDateTime

const val UPDATE_TIME_DELAY_IN_MINUTES = 10

class BitbucklerCachedRepos(
    private val repoRepository: RepoRepository,
    private val preferences: SharedPreferences,
    private val trackedPullRequestsRepo: TrackedPullRequestsRepo,
    private val trackedIssuesRepo: TrackedIssuesRepo,
    private val cachedReposRepository: CachedReposRepository,
) : CachedReposUseCase {

    override suspend fun getReposBy(workspaceSlug: String, projectKey: String) =
        cachedReposRepository.getReposBy(workspaceSlug, projectKey)

    override suspend fun updateTrackedFlagForRepos(repositories: List<Repository>) {
        repoRepository.updateTrackedFlagForRepos(repositories)
    }

    override suspend fun updateIfNeeded(projectKey: String, workspaceSlug: String): List<CachedRepoData> {
        val currentDate = LocalDateTime.now()
        cachedReposRepository.updateTrackCounter()
        val lastUpdateDate = cachedReposRepository.lastUpdateDataBy(workspaceSlug) ?: LocalDateTime.MIN
        return if (currentDate.month == lastUpdateDate.month
            && currentDate.dayOfMonth == lastUpdateDate.dayOfMonth
            && (((currentDate.hour * 60 + currentDate.minute) - (lastUpdateDate.hour * 60 + lastUpdateDate.minute))
                    <= UPDATE_TIME_DELAY_IN_MINUTES)
        ) {
            val localRepos = cachedReposRepository.getReposBy(workspaceSlug, projectKey)
            localRepos.ifEmpty {
                forceUpdate(projectKey, workspaceSlug)
            }
        } else {
            forceUpdate(projectKey, workspaceSlug)
        }
    }

    override suspend fun forceUpdate(
        projectKey: String,
        workspaceSlug: String,
    ): List<CachedRepoData> {
        val cachedRepos = repoRepository.getAllRepositories(workspaceSlug, projectKey).map {
            val updatedDate = LocalDateTime.now()
            it.toCached(workspaceSlug = workspaceSlug, updatedDate)
        }
        cachedReposRepository.replaceAllBy(workspaceSlug, projectKey, cachedRepos)

        return cachedRepos
    }

    override suspend fun tryTrackRepository(uuid: String): Boolean {
        val trackedCount = cachedReposRepository.getTrackedCounter()
        if (trackedCount > 0) {
            preferences.edit().putBoolean(LocalFlags.TRACK_REPOS_FLAG, true).apply()
        }
        if (trackedCount >= TRACKED_REPOSITORY_LIMIT) {
            return false
        }
        setTrackRepositoryFlag(uuid, true)
        return true
    }

    private suspend fun removeTrackedPullRequests(repoUuid: String) {
        trackedPullRequestsRepo.removeBy(repoUuid)
        trackedIssuesRepo.removeBy(repoUuid)
    }

    private suspend fun setTrackRepositoryFlag(uuid: String, trackFlag: Boolean) =
        cachedReposRepository.updateTrackedFlag(uuid, trackFlag)

    override suspend fun unTrackRepository(uuid: String) {
        cachedReposRepository.updateTrackedFlag(uuid, false)
        removeTrackedPullRequests(uuid)
    }

    override suspend fun clear() {
        cachedReposRepository.clear()
    }

    override suspend fun clearTracked() {
        trackedPullRequestsRepo.clearAll()
        trackedIssuesRepo.clear()
    }
}
