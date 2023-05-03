/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 05 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.Filter
import com.akvelon.bitbuckler.model.entity.StatusResponse
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueStatusChangeModel
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueStatusChanges
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueUpdateModel
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueFilter

class IssuesRepository(
    private val api: BitApi,
) {

    suspend fun getUserOpenIssues(
        workspaceSlug: String,
        repositorySlug: String,
        page: String?,
        accountId: String,
    ) = api.getIssues(workspaceSlug, repositorySlug, page, "assignee.account_id=\"$accountId\" AND (state=\"open\" OR state=\"new\")")

    suspend fun getIssues(
        workspaceSlug: String,
        repositorySlug: String,
        page: String?,
        filter: IssueFilter,
        accountId: String = "",
        querySearchText: String = "",
    ) = api.getIssues(workspaceSlug, repositorySlug, page, Filter.issues(filter, accountId, querySearchText))

    suspend fun getIssueById(
        workspaceSlug: String,
        repositorySlug: String,
        issueId: Int,
    ) = api.getIssueById(workspaceSlug, repositorySlug, issueId)

    suspend fun updateIssue(
        workspaceSlug: String,
        repositorySlug: String,
        issueId: Int,
        body: IssueUpdateModel,
    ) = api.updateIssue(workspaceSlug, repositorySlug, issueId, body)

    suspend fun changeIssueStatus(
        workspaceSlug: String,
        repositorySlug: String,
        issueId: Int,
        update: IssueStatusChanges,
    ) = api.changeIssueStatus(workspaceSlug = workspaceSlug,
        repositorySlug = repositorySlug,
        issueId = issueId,
        update = IssueStatusChangeModel(update))

    suspend fun getAttachments(
        workspaceSlug: String,
        repositorySlug: String,
        commentScope: String,
        id: String,
        page: String? = null,
    ) = api.getAttachments(workspaceSlug, repositorySlug, commentScope, id, page)

    suspend fun isIssueWatched(
        workspaceSlug: String,
        repositorySlug: String,
        id: Int,
    ) = StatusResponse.executeRequestAndParseResponse {
        api.isIssueWatched(workspaceSlug = workspaceSlug, repositorySlug = repositorySlug, issueId = id)
    }

    suspend fun watchIssue(
        workspaceSlug: String,
        repositorySlug: String,
        id: Int,
    ) = StatusResponse.executeRequestAndParseResponse {
        api.watchIssue(workspaceSlug = workspaceSlug, repositorySlug = repositorySlug, issueId = id)
    }

    suspend fun unWatchIssue(
        workspaceSlug: String,
        repositorySlug: String,
        id: Int,
    ) = StatusResponse.executeRequestAndParseResponse {
        api.unWatchIssue(workspaceSlug = workspaceSlug, repositorySlug = repositorySlug, issueId = id)
    }

    suspend fun isIssueVoted(
        workspaceSlug: String,
        repositorySlug: String,
        id: Int,
    ) = StatusResponse.executeRequestAndParseResponse {
        api.isIssueVoted(workspaceSlug = workspaceSlug, repositorySlug = repositorySlug, issueId = id)
    }

    suspend fun voteIssue(
        workspaceSlug: String,
        repositorySlug: String,
        id: Int,
    ) = StatusResponse.executeRequestAndParseResponse {
        api.voteIssue(workspaceSlug = workspaceSlug, repositorySlug = repositorySlug, issueId = id)
    }

    suspend fun unVoteIssue(
        workspaceSlug: String,
        repositorySlug: String,
        id: Int,
    ) = StatusResponse.executeRequestAndParseResponse {
        api.unVoteIssue(workspaceSlug = workspaceSlug, repositorySlug = repositorySlug, issueId = id)
    }
}
