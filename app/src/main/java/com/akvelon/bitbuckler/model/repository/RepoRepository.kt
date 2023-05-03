/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 02 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.Filter
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.datasource.database.dao.RecentRepositoryDao
import com.akvelon.bitbuckler.model.datasource.database.dao.ReposLocalStorageDao
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.model.entity.toCached
import com.akvelon.bitbuckler.model.mappers.Mapper

const val TRACKED_REPOSITORY_LIMIT = 5

class RepoRepository(
    private val api: BitApi,
    private val recentRepositoryDao: RecentRepositoryDao,
    private val reposLocalStorageDao: ReposLocalStorageDao
) {

    suspend fun getAllRepositories(
        workspaceSlug: String,
        projectKey: String
    ): MutableList<Repository> {
        val list = mutableListOf<Repository>()
        var repeatFlag = true
        var page = 1
        while (repeatFlag) {
            val loaded = getRepositories(workspaceSlug, projectKey, page.toString()).values
            repeatFlag = loaded.isNotEmpty()
            list.addAll(loaded)
            ++page
        }
        return list
    }

    private suspend fun getRepositories(
        workspaceSlug: String,
        projectKey: String,
        page: String?
    ): PagedResponse<Repository> {
        val repositories = api.getRepositories(
            workspaceSlug,
            Filter.repositoriesByProject(projectKey),
            Filter.UPDATED_ON_DESC,
            page
        )
        val repoLocalData = reposLocalStorageDao.getBy(workspaceSlug, projectKey).associateBy { it.uuid }
        return repositories.copyWithMappingValues {
            Mapper.repositoryMapper(
                it,
                repoLocalData.get(it.uuid)?.isTracked ?: false,
                workspaceSlug
            )
        }
    }

    suspend fun updateTrackedFlagForRepos(repositories: List<Repository>) {
        reposLocalStorageDao.insert(repositories.map { it.toCached(it.workspaceSlug, it.updatedOn) })
    }

    suspend fun getRepositoryDetails(
        workspaceSlug: String,
        repositorySlug: String
    ): Repository {
        val repository = api.getRepositoryDetails(workspaceSlug, repositorySlug)
        return Mapper.repositoryMapper(
            repository,
            reposLocalStorageDao.get(repository.uuid)?.isTracked ?: false,
            workspaceSlug
        )
    }

    suspend fun getCommitsOfBranch(
        workspaceSlug: String,
        repositorySlug: String,
        branch: String,
        page: String? = null
    ) = api.getCommitsOfBranch(workspaceSlug, repositorySlug, branch, page)

    suspend fun getAllRecent() =
        recentRepositoryDao.getAll()

    suspend fun saveToRecent(recentRepository: RecentRepository) =
        recentRepositoryDao.save(recentRepository)

    suspend fun shrinkRecent() =
        recentRepositoryDao.shrinkToLimit()

    suspend fun deleteRecentRepo(recentRepository: RecentRepository) =
        recentRepositoryDao.delete(recentRepository)

    suspend fun clearAllRecent() =
        recentRepositoryDao.clearAll()

    suspend fun getRepositoryPermissions(
        workspaceSlug: String,
        repositorySlug: String,
        page: String? = null,
    ) = api.getRepositoryPermissions(workspaceSlug = workspaceSlug, repositorySlug = repositorySlug, page = page)
}
