package com.akvelon.bitbuckler.domain.repo

import com.akvelon.bitbuckler.extension.noNetworkConnection
import com.akvelon.bitbuckler.model.api.RepositoryDTO
import com.akvelon.bitbuckler.model.entity.DataState
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.args.CommentScope
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.ActivityRepository
import com.akvelon.bitbuckler.model.repository.CommentRepository
import com.akvelon.bitbuckler.model.repository.IssuesRepository

class GetAllUserIssuesWithCommentsUseCase(
    private val issuesRepository: IssuesRepository,
    private val activityRepository: ActivityRepository,
    private val accountRepository: AccountRepository,
    private val commentRepository: CommentRepository,
) {

    suspend fun getUserAllIssues(): DataState<List<TrackedIssue>> {
        return try {
            val user = accountRepository.getCurrentAccount()

            var page = 1
            var repeatFlag = true
            val repositories = arrayListOf<RepositoryDTO>()
            while (repeatFlag) {
                val pageData = activityRepository.getRepositoriesWithIssues(page.toString())
                repositories.addAll(pageData.values)
                repeatFlag = pageData.next != null
                ++page
            }

            val issues = repositories.map { repository ->
                val workspaceSlug = repository.workspace?.slug.orEmpty()
                getRepoIssues(repository = repository.slug, workspace = workspaceSlug, user = user).map { issue ->
                    getIssueWithComments(issue,
                        repositorySlug = repository.slug,
                        workspaceSlug = workspaceSlug,
                        repoUuid = repository.uuid)
                }
            }.flatten()

            DataState.Success(issues)
        } catch (e: Exception) {
            DataState.Error(e.noNetworkConnection)
        }
    }

    private suspend fun getIssueWithComments(
        issue: Issue,
        workspaceSlug: String,
        repositorySlug: String,
        repoUuid: String,
    ): TrackedIssue {
        var page = 1
        var repeatFlag = true
        var commentCount = 0
        while (repeatFlag) {
            val response = commentRepository.getGlobalComments(workspaceSlug = workspaceSlug,
                repositorySlug = repositorySlug,
                commentScope = CommentScope.ISSUES,
                page = page.toString(),
                id = issue.id.toString())
            commentCount += response.values.count { !it.content.raw.isNullOrEmpty() }
            repeatFlag = response.values.isNotEmpty()
            ++page
        }
        return TrackedIssue(issue, repoUuid = repoUuid, workspaceSlug, repositorySlug, commentCount = commentCount)
    }

    private suspend fun getRepoIssues(workspace: String, repository: String, user: User): List<Issue> {
        var page = 1
        var repeatFlag = true
        val issues = arrayListOf<Issue>()
        while (repeatFlag) {
            val pageData = issuesRepository.getUserOpenIssues(workspaceSlug = workspace,
                repositorySlug = repository,
                page = page.toString(),
                accountId = user.accountId)
            issues.addAll(pageData.values)
            repeatFlag = pageData.next != null
            ++page
        }
        return issues
    }
}