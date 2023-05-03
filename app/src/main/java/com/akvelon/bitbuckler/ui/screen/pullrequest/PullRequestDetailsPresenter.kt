/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 23 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest

import com.akvelon.bitbuckler.extension.fromJson
import com.akvelon.bitbuckler.model.datasource.api.bit.response.ErrorResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.args.ChangesArgs
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeAction
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeStrategy
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.PullRequestActionsRepository
import com.akvelon.bitbuckler.model.repository.PullRequestRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.model.repository.analytics.models.AnalyticsParameter
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.PULL_REQUEST_DETAILS
import com.akvelon.bitbuckler.model.repository.pullrequest.TrackedPullRequestsRepo
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.screen.Screen
import com.akvelon.bitbuckler.ui.state.ScreenData
import com.akvelon.bitbuckler.ui.state.ScreenManagerRequestState
import com.akvelon.bitbuckler.ui.state.ScreenStateManager
import com.akvelon.bitbuckler.ui.state.screen.PullRequestDetails
import com.github.terrakok.cicerone.ResultListenerHandler
import com.github.terrakok.cicerone.Router
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Suppress("TooManyFunctions")
@InjectViewState
class PullRequestDetailsPresenter(
    router: Router,
    val pullRequestArgs: PullRequestArgs,
    private val analytics: AnalyticsProvider,
    private val gson: Gson,
    private val accountRepository: AccountRepository,
    private val prRepository: PullRequestRepository,
    private val trackedPullRequestsRepo: TrackedPullRequestsRepo,
    private val prActionsRepository: PullRequestActionsRepository,
) : BasePresenter<PullRequestDetailsView>(router) {

    private var resultHandler: ResultListenerHandler? = null

    private val stateManager = ScreenStateManager(
        {
            launchOnDefault(CoroutineExceptionHandler { _, throwable ->
                launchOnMain { stateCallback.failure(throwable) }
            }) {
                val pullRequests = getPullRequest()
                switchToUi { stateCallback.success(pullRequests) }
            }
        },
        object : ScreenStateManager.ViewController<PullRequestDetails> {

            override fun showEmptyProgress(show: Boolean) {
                viewState.showEmptyProgress(show)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                when (error) {
                    is SocketTimeoutException, is UnknownHostException -> {
                        viewState.setNoInternetConnectionError(show)
                    }
                    is HttpException -> {
                        if (error.code().toString().startsWith("4")) {
                            viewState.setHaveNoAccessError(show)
                        }
                    }
                    is Throwable -> {
                        viewState.setUnknownError(show)
                        NonFatalError.log(error, screenName)
                    }
                }
                viewState.showErrorView(show)
            }

            override fun showData(data: PullRequestDetails) {
                viewState.showData(data)
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

    private val stateCallback: ScreenManagerRequestState<PullRequestDetails> = stateManager

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.PullRequest.PullRequestScreen)

        viewState.apply {
            hideBottomNav()
            showComments()
        }

        stateManager.refresh()
    }

    fun showSummary(show: Boolean) {
        viewState.showSummary(show)
    }

    fun showBranchDialog() {
        viewState.showBranchDialog()
    }

    fun showBuildsDialog() {
        viewState.showBuildsDialog()
    }

    fun onFilesClick(pullRequestTitle: String) {
        pullRequestArgs.title = pullRequestTitle

        router.navigateTo(Screen.changes(ChangesArgs.fromPullRequestArgs(pullRequestArgs)))
    }

    fun onCommitsClick(pullRequestTitle: String) {
        pullRequestArgs.title = pullRequestTitle

        router.navigateTo(Screen.pullRequestCommitList(pullRequestArgs))
    }

    fun refreshPullRequest() = stateManager.refresh()

    fun merge(message: String, closedSourceBranch: Boolean, mergeStrategy: MergeStrategy) =
        launchOnDefault(
            CoroutineExceptionHandler { _, throwable ->
                launchOnMain {
                    viewState.showFullScreenProgress(false)
                    if (throwable is HttpException) { showHttpExceptionMessage(throwable) }
                    else { viewState.showError(throwable) }
                    analytics.report(AnalyticsEvent.PullRequest.PullRequestFailed)
                }
            }
        ) {
            switchToUi { viewState.showFullScreenProgress(true) }

            with(pullRequestArgs) {
                prActionsRepository.merge(
                    workspaceSlug,
                    repositorySlug,
                    id,
                    MergeAction(message, closedSourceBranch, mergeStrategy)
                )
                val pullRequest = getPullRequest()
                trackedPullRequestsRepo.updateState(
                    PullRequestArgs(workspaceSlug, repositorySlug, id),
                    PullRequestState.MERGED
                )
                switchToUi {
                    viewState.showFullScreenProgress(false)
                    viewState.showData(pullRequest.value)
                }

                analytics.report(AnalyticsEvent.PullRequest.PullRequestMergeSuccess)
            }
        }

    fun approve() {
        pullRequestAction(AnalyticsParameter.PrAction.APPROVED)
    }

    fun revokeApproval() {
        pullRequestAction(AnalyticsParameter.PrAction.APPROVE_REVOKED)
    }

    fun requestChanges() {
        pullRequestAction(AnalyticsParameter.PrAction.CHANGES_REQUESTED)
    }

    fun revokeRequestChanges() {
        pullRequestAction(AnalyticsParameter.PrAction.CHANGES_REQUEST_REVOKED)
    }

    fun decline() {
        pullRequestAction(AnalyticsParameter.PrAction.DECLINED)
    }

    fun showDeclineActionDialog() = viewState.showConfirmDeclineDialog()

    fun showMergeActionDialog() = viewState.showMergeActionDialog()

    override fun onDestroy() {
        super.onDestroy()

        resultHandler?.dispose()
        stateManager.release()
    }

    private fun pullRequestAction(actionName: String) = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain {
                viewState.showFullScreenProgress(false)
                if (throwable is HttpException) { showHttpExceptionMessage(throwable) }
                else { viewState.showError(throwable) }
            }
        }
    ) {
        switchToUi { viewState.showFullScreenProgress(true) }

        with(pullRequestArgs) {
            when (actionName) {
                AnalyticsParameter.PrAction.APPROVED -> {
                    prActionsRepository.approve(workspaceSlug, repositorySlug, id)
                    analytics.report(AnalyticsEvent.PullRequest.PullRequestApprove)
                }
                AnalyticsParameter.PrAction.APPROVE_REVOKED -> {
                    prActionsRepository.revokeApproval(workspaceSlug, repositorySlug, id)
                    analytics.report(AnalyticsEvent.PullRequest.PullRequestUnApprove)
                }
                AnalyticsParameter.PrAction.CHANGES_REQUESTED -> {
                    prActionsRepository.requestChanges(workspaceSlug, repositorySlug, id)
                }
                AnalyticsParameter.PrAction.CHANGES_REQUEST_REVOKED -> {
                    prActionsRepository.revokeRequestChanges(workspaceSlug, repositorySlug, id)
                }
                AnalyticsParameter.PrAction.DECLINED -> {
                    prActionsRepository.decline(workspaceSlug, repositorySlug, id)
                    trackedPullRequestsRepo.updateState(
                        PullRequestArgs(workspaceSlug, repositorySlug, id),
                        PullRequestState.DECLINED
                    )
                    analytics.report(AnalyticsEvent.PullRequest.PullRequestDecline)
                }
            }
            val pullRequest = getPullRequest()
            switchToUi {
                viewState.showFullScreenProgress(false)
                viewState.showData(pullRequest.value)
            }
        }
    }

    private suspend fun getPullRequest() = with(pullRequestArgs) {
        val pullRequest = prRepository.getPullRequestById(workspaceSlug, repositorySlug, id)
        val account = accountRepository.getCurrentAccount()
        val builds = prRepository.getPullRequestStatuses(workspaceSlug, repositorySlug, id)

        ScreenData(
            PullRequestDetails(pullRequest, account, builds.values)
        )

    }

    private fun showHttpExceptionMessage(error: HttpException) {
        error.response()?.errorBody()?.string()?.let { response ->
            viewState.showError(
                gson.fromJson<ErrorResponse>(response).error.message
            )
        } ?: run {
            viewState.showError(error)
        }
    }

    suspend fun showUsersWhoApproved() = with(pullRequestArgs) {
        launchOnIO {
            val users = prRepository.getUsersWhoApproved(
                workspaceSlug,
                repositorySlug,
                id
            )
            switchToUi { viewState.showUsersWhoApproved(users) }
        }
    }

    companion object {
        private const val screenName = PULL_REQUEST_DETAILS
    }
}
