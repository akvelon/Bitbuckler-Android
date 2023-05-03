/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler.model.datasource.api.bit

import com.akvelon.bitbuckler.model.api.RepositoryDTO
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.*
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.comment.NewComment
import com.akvelon.bitbuckler.model.entity.issueTracker.Attachment
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueStatusChangeModel
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueUpdateModel
import com.akvelon.bitbuckler.model.entity.participant.Participant
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeAction
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileStat
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.entity.search.CodeSearchResult
import com.akvelon.bitbuckler.model.entity.source.Source
import com.akvelon.bitbuckler.model.entity.statuses.Status
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

@Suppress("TooManyFunctions")
interface BitApi {

    companion object {
        const val ver = "2.0"
    }

    @GET("$ver/user")
    suspend fun getAccount(): User

    @GET("$ver/user/emails")
    suspend fun getUserEmails(
        @Query("page") page: String? = null
    ): PagedResponse<Email>

    @GET("$ver/users/{selected_user}")
    suspend fun getAccountByUUID(
        @Path("selected_user") uuid: String
    ): User

    @GET("$ver/workspaces")
    suspend fun getWorkspaces(
        @Query("page") page: String? = null
    ): PagedResponse<Workspace>

    @GET("$ver/workspaces/{workspace}/projects")
    suspend fun getProjects(
        @Path("workspace") workspaceSlug: String,
        @Query("page") page: String? = null
    ): PagedResponse<Project>

    @GET("$ver/repositories")
    suspend fun getPublicRepositories(
        @Query("q") filter: String,
        @Query("role") role: String,
        @Query("page") page: String? = null
    ): PagedResponse<RepositoryDTO>

    @GET("$ver/repositories/{workspace}")
    suspend fun getRepositories(
        @Path("workspace") workspaceSlug: String,
        @Query("q") filter: String,
        @Query("sort") sort: String,
        @Query("page") page: String? = null
    ): PagedResponse<RepositoryDTO>

    @GET("$ver/repositories/{workspace}/{repo_slug}")
    suspend fun getRepositoryDetails(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String
    ): RepositoryDTO

    @GET("$ver/repositories/{workspace}/{repo_slug}/commits/{revision}")
    suspend fun getCommitsOfBranch(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("revision") branch: String,
        @Query("page") page: String? = null
    ): PagedResponse<Commit>

    @GET("$ver/repositories/{workspace}/{repo_slug}/commit/{commit}")
    suspend fun getCommitDetails(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("commit") commitHash: String,
    ): Commit

    @GET("$ver/repositories/{workspace}/{repo_slug}/commit/{commit}/statuses")
    suspend fun getCommitStatuses(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("commit") commitHash: String,
    ): PagedResponse<Status>

    @GET("$ver/repositories/{workspace}/{repo_slug}/diff/{spec}?binary=false")
    suspend fun getCommitDiff(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("spec") commitHash: String,
    ): ResponseBody

    @GET("$ver/repositories/{workspace}/{repo_slug}/diffstat/{spec}?binary=false")
    suspend fun getCommitDiffStat(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("spec") commitHash: String,
    ): PagedResponse<FileStat>

    @GET("$ver/repositories/{workspace}/{repo_slug}/src/{commit}/{path}")
    suspend fun getFiles(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("commit") commitHash: String,
        @Path("path") filePath: String,
        @Query("page") page: String? = null
    ): PagedResponse<Source>

    @GET("$ver/repositories/{workspace}/{repo_slug}/src/{commit}/{path}")
    suspend fun getFileContent(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("commit") commitHash: String,
        @Path("path") filePath: String
    ): ResponseBody

    @GET("$ver/repositories/{workspace}/{repo_slug}/pullrequests")
    suspend fun getPullRequests(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Query("page") page: String? = null,
        @Query("q") filter: String
    ): PagedResponse<PullRequest>

    @GET("$ver/repositories/{workspace}/{repo_slug}/pullrequests")
    suspend fun getPullRequests(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Query("page") page: String? = null,
    ): PagedResponse<PullRequest>

    @GET("$ver/pullrequests/{selected_user}")
    suspend fun getPullRequestsForUser(
        @Path("selected_user") selectedUser: String,
        @Query("page") page: String? = null,
        @Query("q") filter: String
    ): PagedResponse<PullRequest>

    @GET("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}")
    suspend fun getPullRequestById(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): PullRequest

    @GET("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/statuses")
    suspend fun getPullRequestStatuses(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): PagedResponse<Status>

    @Suppress("LongParameterList")
    @GET("$ver/repositories/{workspace}/{repo_slug}/{scope}/{id}/comments")
    suspend fun getComments(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("scope") commentScope: String,
        @Path("id") id: String,
        @Query("q") filter: String,
        @Query("page") page: String? = null
    ): PagedResponse<Comment>

    @Suppress("LongParameterList")
    @GET("$ver/repositories/{workspace}/{repo_slug}/{scope}/{id}/attachments")
    suspend fun getAttachments(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("scope") commentScope: String,
        @Path("id") id: String,
        @Query("page") page: String?,
    ): PagedResponse<Attachment>

    @POST("$ver/repositories/{workspace}/{repo_slug}/{scope}/{id}/comments")
    suspend fun addNewComment(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("scope") commentScope: String,
        @Path("id") id: String,
        @Body comment: NewComment
    ): Comment

    @Suppress("LongParameterList")
    @PUT("$ver/repositories/{workspace}/{repo_slug}/{scope}/{id}/comments/{comment_id}")
    suspend fun editComment(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("scope") commentScope: String,
        @Path("id") scopeId: String,
        @Path("comment_id") commentId: Int,
        @Body comment: NewComment
    ): Comment

    @DELETE("$ver/repositories/{workspace}/{repo_slug}/{scope}/{id}/comments/{comment_id}")
    suspend fun deleteComment(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("scope") commentScope: String,
        @Path("id") id: String,
        @Path("comment_id") commentId: Int
    ): Response<Void>

    @POST("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/merge")
    suspend fun merge(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int,
        @Body mergeAction: MergeAction
    ): PullRequest

    @POST("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/approve")
    suspend fun approve(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): Participant

    @DELETE("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/approve")
    suspend fun revokeApproval(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): Response<Void>

    @POST("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/request-changes")
    suspend fun requestChanges(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): Participant

    @DELETE("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/request-changes")
    suspend fun revokeRequestChanges(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): Response<Void>

    @POST("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/decline")
    suspend fun decline(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): PullRequest

    @GET("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/diff?binary=false")
    suspend fun getPullRequestDiff(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): ResponseBody

    @GET("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/diffstat")
    suspend fun getPullRequestDiffStat(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int
    ): PagedResponse<FileStat>

    @GET("$ver/repositories/{workspace}/{repo_slug}/refs/branches")
    suspend fun getBranches(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Query("page") page: String? = null
    ): PagedResponse<Branch>

    @GET("$ver/repositories/{workspace}/{repo_slug}/refs/tags")
    suspend fun getTags(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Query("page") page: String? = null
    ): PagedResponse<Branch>

    @GET("$ver/repositories/{workspace}/{repo_slug}/issues")
    suspend fun getIssues(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Query("page") page: String? = null,
        @Query("q") filter: String
    ): PagedResponse<Issue>

    @GET("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}")
    suspend fun getIssueById(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int
    ): Issue

    @PUT("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}")
    suspend fun updateIssue(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
        @Body body: IssueUpdateModel
    ): Issue

    @POST("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}/changes")
    suspend fun changeIssueStatus(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
        @Body update: IssueStatusChangeModel,
    ): IssueStatusChangeModel

    @GET("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}/watch")
    suspend fun isIssueWatched(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
    ): Response<Void>

    @PUT("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}/watch")
    suspend fun watchIssue(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
    ): Response<Void>

    @DELETE("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}/watch")
    suspend fun unWatchIssue(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
    ): Response<Void>

    @GET("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}/vote")
    suspend fun isIssueVoted(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
    ): Response<Void>

    @PUT("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}/vote")
    suspend fun voteIssue(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
    ): Response<Void>

    @DELETE("$ver/repositories/{workspace}/{repo_slug}/issues/{issue_id}/vote")
    suspend fun unVoteIssue(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("issue_id") issueId: Int,
    ): Response<Void>

    @GET("$ver/repositories/{workspace}/{repo_slug}/pullrequests/{pull_request_id}/commits")
    suspend fun getPullRequestCommits(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Path("pull_request_id") pullRequestId: Int,
        @Query("page") page: String? = null
    ): PagedResponse<Commit>

    @GET("$ver/workspaces/{workspace}/search/code")
    suspend fun searchForCodeInWorkspace(
        @Path("workspace") workspaceSlug: String,
        @Query("search_query") searchQuery: String,
        @Query("page") page: String? = null
    ): PagedResponse<CodeSearchResult>

    @GET("$ver/workspaces/{workspace}/members")
    suspend fun getWorkspaceMembers(
        @Path("workspace") workspaceSlug: String,
        @Query("page") page: String? = null
    ): PagedResponse<WorkspaceMembership>

    @GET("$ver/workspaces/{workspace}/permissions/repositories/{repo_slug}")
    suspend fun getRepositoryPermissions(
        @Path("workspace") workspaceSlug: String,
        @Path("repo_slug") repositorySlug: String,
        @Query("page") page: String? = null,
    ): PagedResponse<RepositoryPermission>
}
