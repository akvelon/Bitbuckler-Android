/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 21 March 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.issues.details

import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.args.CommentScope
import com.akvelon.bitbuckler.model.entity.args.IssueArgs
import com.akvelon.bitbuckler.model.entity.issueTracker.Attachment
import com.akvelon.bitbuckler.model.entity.issueTracker.update.ChangeModel
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueStatusChanges
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueUpdateModel
import com.akvelon.bitbuckler.model.entity.issueTracker.update.IssueUserUpdateModel
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueKind
import com.akvelon.bitbuckler.model.entity.repository.issue.IssuePriority
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueState
import com.akvelon.bitbuckler.model.repository.AuthRepository
import com.akvelon.bitbuckler.model.repository.IssuesRepository
import com.akvelon.bitbuckler.model.repository.RepoRepository
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.ISSUE_DETAILS
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.billing.BillingUseCase
import com.akvelon.bitbuckler.ui.pagination.Paginator
import com.akvelon.bitbuckler.ui.pagination.PaginatorRequestState
import com.akvelon.bitbuckler.ui.state.ScreenData
import com.akvelon.bitbuckler.ui.state.ScreenManagerRequestState
import com.akvelon.bitbuckler.ui.state.ScreenStateManager
import com.akvelon.bitbuckler.ui.state.screen.IssueDetails
import com.github.terrakok.cicerone.ResultListenerHandler
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.presenterScope
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
@Suppress("TooManyFunctions")
class IssueDetailsPresenter(
    router: Router,
    val issueArgs: IssueArgs,
    private val issuesRepository: IssuesRepository,
    private val authRepository: AuthRepository,
    private val repoRepository: RepoRepository,
    val billingUseCase: BillingUseCase
) : BasePresenter<IssueDetailsView>(router) {

    private var resultHandler: ResultListenerHandler? = null
    private var currentIssueWatches = 0
    private var currentIssueVotes = 0
    private var repoCollaborators = emptyList<User?>()

    private val stateManager = ScreenStateManager(
        {
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { screenManagerCallback.failure(throwable) }
                }
            ) {
                val issue = getIssue()
                switchToUi { screenManagerCallback.success(issue) }
            }
        },
        object : ScreenStateManager.ViewController<IssueDetails> {

            override fun showEmptyProgress(show: Boolean) {
                viewState.showEmptyProgress(show)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                when (error) {
                    is SocketTimeoutException, is UnknownHostException ->
                        viewState.setNoInternetConnectionError(show)
                    is Throwable -> {
                        viewState.setUnknownError(show)
                        NonFatalError.log(error, screenName)
                    }
                }
                viewState.showErrorView(show)
            }

            override fun showData(data: IssueDetails) {
                viewState.showData(data)
                currentIssueVotes = data.issue.votes
                currentIssueWatches = data.issue.watches
                showWatchesAndVotes()
                showAssigneeDetails(data)
                getCollaborators(data.issue.assignee)

                viewState.showComments()
            }

            override fun showEmptyView(show: Boolean) {
            }

            override fun showErrorMessage(error: Throwable) {
                viewState.showError(error)
            }

            override fun showRefreshProgress(show: Boolean) {
                viewState.showRefreshProgress(show)
            }
        }
    )

    private fun showAssigneeDetails(data: IssueDetails) {
        val collaborators = repoCollaborators.toMutableList()
        collaborators.removeAll { it?.accountId == data.issue.assignee?.accountId }
        val assignees = listOf(data.issue.assignee) + if (data.issue.assignee != null)
            collaborators.filterNotNull()
        else
            collaborators
        viewState.showAssigneeDetails(assignees)
    }

    private val attachmentsPager = Paginator({ page ->
        launchOnDefault(
            CoroutineExceptionHandler { _, throwable ->
                launchOnMain { paginatorRequestState.failure(throwable) }
            }
        ) {
            val response = issuesRepository.getAttachments(
                workspaceSlug = issueArgs.workspaceSlug,
                repositorySlug = issueArgs.repositorySlug,
                commentScope = CommentScope.ISSUES.slug,
                id = issueArgs.id.toString(),
                page = page
            )
            switchToUi { paginatorRequestState.success(response) }
        }
    },
        object : Paginator.ViewController<Attachment> {
            override fun showEmptyProgress(show: Boolean) {}

            override fun showEmptyError(show: Boolean, error: Throwable?) {}

            override fun showEmptyView(show: Boolean) {
                viewState.showFiles(false, emptyList())
            }

            override fun showData(show: Boolean, data: List<Attachment>, scrollToTop: Boolean) {
                viewState.showFiles(show, data)
            }

            override fun showErrorMessage(error: Throwable) {}

            override fun showRefreshProgress(show: Boolean) {}

            override fun showPageProgress(show: Boolean) {}

            override fun showPageProgressError(show: Boolean, error: Throwable?) {}
        }
    )

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Attachment>> = attachmentsPager

    private val screenManagerCallback: ScreenManagerRequestState<IssueDetails> = stateManager

    override fun onFirstViewAttach() {
        stateManager.refresh()
        attachmentsPager.refresh()
        checkIssueStatuses()

        billingUseCase.isSubscriptionActive
            .onEach(viewState::showSubscriptionStatus)
            .launchIn(presenterScope)
    }

    fun getProductDetails() {
        billingUseCase.getProductDetails()
    }

    private fun getCollaborators(assignee: User?) {
        if (repoCollaborators.isEmpty()) {
            presenterScope.launch {
                val response = repoRepository.getRepositoryPermissions(
                    workspaceSlug = issueArgs.workspaceSlug,
                    repositorySlug = issueArgs.repositorySlug
                ).values.map {
                    it.user
                }.toMutableList<User?>()

                response.removeAll { it?.accountId == assignee?.accountId }
                response.add(0, assignee)

                repoCollaborators = response
                viewState.showAssigneeDetails(response)
            }
        }
    }

    private fun checkIssueStatuses() {
        presenterScope.launch {
            val isIssueVoted = issuesRepository.isIssueVoted(
                workspaceSlug = issueArgs.workspaceSlug,
                repositorySlug = issueArgs.repositorySlug,
                id = issueArgs.id
            )
            viewState.isIssueVoted(isIssueVoted.status)
        }
        presenterScope.launch {
            val isIssueWatched = issuesRepository.isIssueWatched(
                workspaceSlug = issueArgs.workspaceSlug,
                repositorySlug = issueArgs.repositorySlug,
                id = issueArgs.id
            )
            viewState.isIssueWatched(isIssueWatched.status)
        }
    }

    fun changeIssueAssignee(userId: String) {
        presenterScope.launch {
            issuesRepository.updateIssue(
                workspaceSlug = issueArgs.workspaceSlug,
                repositorySlug = issueArgs.repositorySlug,
                issueId = issueArgs.id,
                body = IssueUpdateModel(IssueUserUpdateModel(userId))
            )
            stateManager.refresh()
        }
    }

    fun updateIssue(
        status: IssueState? = null,
        type: IssueKind? = null,
        priority: IssuePriority? = null,
    ) {
        presenterScope.launch {
            issuesRepository.changeIssueStatus(
                workspaceSlug = issueArgs.workspaceSlug,
                repositorySlug = issueArgs.repositorySlug,
                issueId = issueArgs.id,
                update = IssueStatusChanges(
                    priority = if (priority != null) ChangeModel(priority.name.lowercase()) else null,
                    state = if (status != null) ChangeModel(status.value.lowercase()) else null,
                    kind = if (type != null) ChangeModel(type.name.lowercase()) else null,
                )
            )
            stateManager.refresh()
        }
    }

    private fun showWatchesAndVotes() {
        viewState.setIssueWatchesAndVotes(votes = currentIssueVotes, watches = currentIssueWatches)
    }

    fun toggleWatchStatus(status: Boolean) {
        presenterScope.launch {
            val responseStatus = if (status)
                issuesRepository.unWatchIssue(
                    workspaceSlug = issueArgs.workspaceSlug,
                    repositorySlug = issueArgs.repositorySlug,
                    id = issueArgs.id
                )
            else
                issuesRepository.watchIssue(
                    workspaceSlug = issueArgs.workspaceSlug,
                    repositorySlug = issueArgs.repositorySlug,
                    id = issueArgs.id
                )
            if (responseStatus.status) {
                viewState.isIssueWatched(!status)
                currentIssueWatches = if (status)
                    currentIssueWatches - 1
                else
                    currentIssueWatches + 1
                showWatchesAndVotes()
            }
        }
    }

    fun toggleVoteStatus(status: Boolean) {
        presenterScope.launch {
            val responseStatus = if (status)
                issuesRepository.unVoteIssue(
                    workspaceSlug = issueArgs.workspaceSlug,
                    repositorySlug = issueArgs.repositorySlug,
                    id = issueArgs.id
                )
            else
                issuesRepository.voteIssue(
                    workspaceSlug = issueArgs.workspaceSlug,
                    repositorySlug = issueArgs.repositorySlug,
                    id = issueArgs.id
                )
            if (responseStatus.status) {
                viewState.isIssueVoted(!status)
                currentIssueVotes = if (status)
                    currentIssueVotes - 1
                else
                    currentIssueVotes + 1
                showWatchesAndVotes()
            }
        }
    }

    fun loadNextFilesPage() {
        attachmentsPager.loadNewPage()
    }

    fun refreshIssue() = stateManager.refresh()

    override fun onDestroy() {
        super.onDestroy()

        resultHandler?.dispose()
        stateManager.release()
    }

    private suspend fun getIssue() = with(issueArgs) {
        val issue = issuesRepository.getIssueById(workspaceSlug, repositorySlug, id)
        ScreenData(IssueDetails(issue))
    }

    fun requestLoadWebviewPage(url: String) {
        viewState.loadWebViewPage(url, mapOf("Authorization" to "Bearer ${authRepository.getAccessToken()}"))
    }

    companion object {
        private const val screenName = ISSUE_DETAILS
    }
}
