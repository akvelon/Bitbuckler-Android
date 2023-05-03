/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 11 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeAction

class PullRequestActionsRepository(
    private val api: BitApi
) {

    suspend fun merge(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int,
        mergeAction: MergeAction
    ) = api.merge(workspaceSlug, repositorySlug, pullRequestId, mergeAction)

    suspend fun approve(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.approve(workspaceSlug, repositorySlug, pullRequestId)

    suspend fun revokeApproval(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.revokeApproval(workspaceSlug, repositorySlug, pullRequestId)

    suspend fun requestChanges(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.requestChanges(workspaceSlug, repositorySlug, pullRequestId)

    suspend fun revokeRequestChanges(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.revokeRequestChanges(workspaceSlug, repositorySlug, pullRequestId)

    suspend fun decline(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.decline(workspaceSlug, repositorySlug, pullRequestId)
}
