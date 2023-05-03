package com.akvelon.bitbuckler.model.repository.pullrequest

import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import kotlinx.coroutines.flow.Flow

interface TrackedPullRequestsRepo {

    suspend fun addAll(pullRequests: List<TrackedPullRequest>)

    suspend fun updateState(args: PullRequestArgs, state: PullRequestState)

    suspend fun removeBy(repoUuid: String)

    fun getAll(): Flow<List<TrackedPullRequest>>

    suspend fun getAllAsync(): List<TrackedPullRequest>

    suspend fun clearAll()
}