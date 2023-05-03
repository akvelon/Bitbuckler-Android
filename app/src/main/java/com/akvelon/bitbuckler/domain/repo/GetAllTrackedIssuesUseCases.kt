package com.akvelon.bitbuckler.domain.repo

import com.akvelon.bitbuckler.extension.noNetworkConnection
import com.akvelon.bitbuckler.model.entity.CachedRepoData
import com.akvelon.bitbuckler.model.entity.DataState
import com.akvelon.bitbuckler.model.entity.args.CommentScope
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueFilter
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.CommentRepository
import com.akvelon.bitbuckler.model.repository.IssuesRepository
import com.akvelon.bitbuckler.model.repository.local.CachedReposRepository

class GetAllTrackedIssuesUseCases(
    private val issuesRepo: IssuesRepository,
    private val accountRepository: AccountRepository,
    private val cachedReposRepository: CachedReposRepository,
    private val commentsRepository: CommentRepository,
) {

    suspend fun getAll(): DataState<List<TrackedIssue>> {
        return try {
            val trackedRepos = cachedReposRepository.getTrackedRepos()
            val trackedIssues = trackedRepos.filter {
                it.hasIssues
            }.map { repo ->
                fetchAllIssues(repo)
            }.flatten()
            DataState.Success(trackedIssues)
        } catch (e: Exception) {
            DataState.Error(e.noNetworkConnection)
        }
    }

    private suspend fun fetchAllIssues(data: CachedRepoData): List<TrackedIssue> {
        var page = 1
        var repeatFlag = true
        val currentUser = accountRepository.getCurrentAccount()
        val array = arrayListOf<TrackedIssue>()
        while (repeatFlag) {
            val items = issuesRepo.getIssues(
                workspaceSlug = data.workspaceSlug,
                repositorySlug = data.slug,
                page = page.toString(),
                filter = IssueFilter.OPEN,
                accountId = currentUser.accountId
            ).values.map { issue ->
                getIssueWithComments(data, issue)
            }
            array.addAll(items)
            repeatFlag = items.isNotEmpty()
            ++page
        }
        return array
    }

    private suspend fun getIssueWithComments(
        data: CachedRepoData,
        issue: Issue,
    ): TrackedIssue {
        var page = 1
        var repeatFlag = true
        var commentCount = 0
        while (repeatFlag) {
            val response = commentsRepository.getGlobalComments(
                workspaceSlug = data.workspaceSlug,
                repositorySlug = data.slug,
                commentScope = CommentScope.ISSUES,
                page = page.toString(),
                id = issue.id.toString()
            )
            commentCount += response.values.count { !it.content.raw.isNullOrEmpty() }
            repeatFlag = response.values.isNotEmpty()
            ++page
        }
        return TrackedIssue(issue, repoUuid = data.uuid, data.workspaceSlug, data.slug, commentCount = commentCount)
    }
}