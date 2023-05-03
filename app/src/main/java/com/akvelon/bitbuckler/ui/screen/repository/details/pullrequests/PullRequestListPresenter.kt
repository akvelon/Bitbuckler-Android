/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests

import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.repository.PullRequestRepository
import com.akvelon.bitbuckler.model.repository.SettingsRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.PULL_REQUEST_LIST
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.pagination.Paginator
import com.akvelon.bitbuckler.ui.pagination.PaginatorRequestState
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
class PullRequestListPresenter(
    router: Router,
    private val slugs: Slugs,
    private val analytics: AnalyticsProvider,
    private val prRepository: PullRequestRepository,
    private val settingsRepository: SettingsRepository
) : BasePresenter<PullRequestListView>(router) {

    private val paginator = Paginator(
        { page ->
            launchOnDefault(CoroutineExceptionHandler { _, throwable ->
                launchOnMain { paginatorRequestState.failure(throwable) }
            }) {
                val prs = getPullRequests(page)
                switchToUi { paginatorRequestState.success(prs) }
            }
        },
        object : Paginator.ViewController<PullRequest> {

            override fun showEmptyProgress(show: Boolean) =
                viewState.showEmptyProgress(show)

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

            override fun showEmptyView(show: Boolean) =
                viewState.showEmptyView(show)

            override fun showData(show: Boolean, data: List<PullRequest>, scrollToTop: Boolean) =
                viewState.showPullRequests(show, data)

            override fun showErrorMessage(error: Throwable) =
                viewState.showErrorMessage(error)

            override fun showRefreshProgress(show: Boolean) =
                viewState.showRefreshProgress(show)

            override fun showPageProgress(show: Boolean) =
                viewState.showPageProgress(show)

            override fun showPageProgressError(show: Boolean, error: Throwable?) {
                error?.let { viewState.showErrorMessage(it) }
                viewState.showPageProgressError(show)
            }
        }
    )

    var paginatorRequestState: PaginatorRequestState<PagedResponse<PullRequest>> = paginator

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Repository.RepositoryPullRequestScreen)

        refreshPullRequests()
    }

    fun refreshPullRequests() = paginator.refresh()

    fun loadNextPullRequestsPage() {
        paginator.loadNewPage()
    }

    fun getLastPullRequestState() = settingsRepository.getLastPullRequestState()

    fun onLastPrStateChanged(state: PullRequestState) {
        settingsRepository.setLastPullRequestState(state)
        refreshPullRequests()

        when (state) {
            PullRequestState.OPEN -> analytics.report(AnalyticsEvent.PullRequestsScreen.PullRequestFilterOpen)
            PullRequestState.MERGED -> analytics.report(AnalyticsEvent.PullRequestsScreen.PullRequestFilterMerged)
            PullRequestState.DECLINED -> analytics.report(AnalyticsEvent.PullRequestsScreen.PullRequestFilterDeclined)
            else -> Unit
        }
    }

    fun onPullRequestClick(pullRequest: PullRequest) {
        router.navigateTo(
            Screen.pullRequestDetails(
                PullRequestArgs(
                    slugs.workspace,
                    slugs.repository,
                    pullRequest.id
                )
            )
        )
    }

    override fun onDestroy() = paginator.release()

    private suspend fun getPullRequests(page: String?) = prRepository.getPullRequests(
        slugs.workspace,
        slugs.repository,
        page,
        getLastPullRequestState()
    )

    companion object {
        private const val screenName = PULL_REQUEST_LIST
    }
}
