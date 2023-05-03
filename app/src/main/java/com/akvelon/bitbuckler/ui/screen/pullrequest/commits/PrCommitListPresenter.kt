/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest.commits

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.args.CommitDetailsArgs
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.model.repository.PullRequestRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
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
@Suppress("TooManyFunctions")
class PrCommitListPresenter(
    router: Router,
    val pullRequestArgs: PullRequestArgs,
    private val analytics: AnalyticsProvider,
    private val pullRequestRepository: PullRequestRepository
) : BasePresenter<PrCommitListView>(router) {

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val response = getCommits(page)
                switchToUi { paginatorRequestState.success(response) }
            }
        },
        object : Paginator.ViewController<Commit> {
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

            override fun showEmptyView(show: Boolean) {
                viewState.showEmptyView(show)
            }

            override fun showData(show: Boolean, data: List<Commit>, scrollToTop: Boolean) {
                viewState.showCommits(show, data)
            }

            override fun showErrorMessage(error: Throwable) {
                viewState.showErrorMessage(error)
            }

            override fun showRefreshProgress(show: Boolean) {
                viewState.showRefreshProgress(show)
            }

            override fun showPageProgress(show: Boolean) {
                viewState.showPageProgress(show)
            }

            override fun showPageProgressError(show: Boolean, error: Throwable?) {
                viewState.showPageProgressError(show)
            }
        }
    )

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Commit>> = paginator

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.PullRequest.PullRequestCommitsScreen)
        viewState.hideBottomNav()
        refreshCommits()
    }

    fun refreshCommits() {
        paginator.refresh()
    }

    fun loadNextCommitsPage() {
        paginator.loadNewPage()
    }

    fun onCommitClick(commit: Commit) {
        router.navigateTo(
            Screen.commitDetails(
                CommitDetailsArgs(
                    pullRequestArgs.workspaceSlug,
                    pullRequestArgs.repositorySlug,
                    commit.hash,
                )
            )
        )
    }

    private suspend fun getCommits(page: String?) = with(pullRequestArgs) {
        pullRequestRepository.getPullRequestCommits(
            workspaceSlug,
            repositorySlug,
            id,
            page
        )
    }

    companion object {
        private const val screenName = ScreenName.PR_COMMIT_LIST
    }
}
