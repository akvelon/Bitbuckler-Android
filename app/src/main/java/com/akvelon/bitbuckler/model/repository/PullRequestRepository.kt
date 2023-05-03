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
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState

class PullRequestRepository(
    private val api: BitApi
) {

    suspend fun getPullRequests(
        workspaceSlug: String,
        repositorySlug: String,
        page: String?,
        state: PullRequestState = PullRequestState.OPEN
    ) = api.getPullRequests(workspaceSlug, repositorySlug, page, Filter.prsInRepo(state))

    suspend fun getPullRequestById(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.getPullRequestById(workspaceSlug, repositorySlug, pullRequestId)

    suspend fun getUsersWhoApproved(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ): List<String> {
        val participants =
            api.getPullRequestById(workspaceSlug, repositorySlug, pullRequestId).participants
        val users = mutableListOf<String>()
        participants?.forEach { participant ->
            if (participant.approved) users.add(participant.user.nickname)
        }

        return users
    }

    suspend fun getPullRequestStatuses(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.getPullRequestStatuses(workspaceSlug, repositorySlug, pullRequestId)

    suspend fun getPullRequestCommits(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int,
        page: String? = null
    ) = api.getPullRequestCommits(workspaceSlug, repositorySlug, pullRequestId, page)
}
