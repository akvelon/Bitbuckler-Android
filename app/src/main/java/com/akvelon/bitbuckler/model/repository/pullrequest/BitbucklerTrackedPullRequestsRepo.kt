package com.akvelon.bitbuckler.model.repository.pullrequest

import com.akvelon.bitbuckler.model.datasource.database.dao.TrackedPullRequestDao
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest

class BitbucklerTrackedPullRequestsRepo(
    private val dao: TrackedPullRequestDao,
) : TrackedPullRequestsRepo {

    override suspend fun addAll(pullRequests: List<TrackedPullRequest>) =
        dao.insert(pullRequests)

    override suspend fun updateState(args: PullRequestArgs, state: PullRequestState) =
        dao.updateState(
            workspaceSlug = args.workspaceSlug,
            repositorySlug = args.repositorySlug,
            idInRepository = args.id,
            state = state)

    override suspend fun removeBy(repoUuid: String) =
        dao.removeBy(repoUuid)

    override fun getAll() =
        dao.getAll()

    override suspend fun getAllAsync(): List<TrackedPullRequest> {
        return dao.getAllAsync()
    }

    override suspend fun clearAll() =
        dao.clear()
}