package com.akvelon.bitbuckler.model.repository.local

import com.akvelon.bitbuckler.model.datasource.database.dao.ReposLocalStorageDao
import com.akvelon.bitbuckler.model.entity.CachedRepoData
import java.util.concurrent.atomic.AtomicInteger

class BitbucklerCachedReposRepository(
    private val reposLocalDao: ReposLocalStorageDao
): CachedReposRepository {
    var trackedRepoCounter: AtomicInteger = AtomicInteger(-1)

    override suspend fun getReposBy(workspaceSlug: String, projectKey: String): List<CachedRepoData> {
        updateTrackCounter()
        return reposLocalDao.getBy(workspaceSlug, projectKey)
    }

    override suspend fun getTrackedRepos() =
        reposLocalDao.getAllTracked()

    override suspend fun getRepo(uuid: String) =
        reposLocalDao.get(uuid)

    override suspend fun replaceAllBy(workspaceSlug: String, projectKey: String, repos: List<CachedRepoData>) {
        reposLocalDao.remove(workspaceSlug, projectKey)
        reposLocalDao.insert(repos)
    }

    override suspend fun lastUpdateDataBy(workspaceSlug: String) =
        reposLocalDao.lastUpdatedBy(workspaceSlug)

    override suspend fun getTrackedCounter(): Int {
        return trackedRepoCounter.get()
    }

    override suspend fun updateTrackCounter() {
        val counter = reposLocalDao.getTrackedCounter()
        trackedRepoCounter.set(counter)
    }

    override suspend fun updateTrackedFlag(uuid: String, isTracked: Boolean){
        reposLocalDao.updateTrackedFlag(uuid, isTracked)
        updateTrackCounter()
    }

    override suspend fun clear() =
        reposLocalDao.clear()
}