/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.branches

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.BRANCH_LIST
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.repository.BranchRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.pagination.Paginator
import com.akvelon.bitbuckler.ui.pagination.PaginatorRequestState
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
class BranchListPresenter(
    router: Router,
    private val slugs: Slugs,
    private val analytics: AnalyticsProvider,
    private val branchRepository: BranchRepository
) : BasePresenter<BranchListView>(router) {

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val result = getBranches(page)
                switchToUi { paginatorRequestState.success(result) }
            }
        },
        object : Paginator.ViewController<Branch> {

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

            override fun showData(show: Boolean, data: List<Branch>, scrollToTop: Boolean) =
                viewState.showBranches(show, data)

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

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Branch>> = paginator

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Repository.RepositoryBranchesScreen)

        refreshBranches()
    }

    fun refreshBranches() = paginator.refresh()

    fun loadNextBranchesPage() {
        paginator.loadNewPage()
    }

    override fun onDestroy() = paginator.release()

    private suspend fun getBranches(page: String?) = branchRepository.getBranches(
        slugs.workspace,
        slugs.repository,
        page
    )

    companion object {
        private const val screenName = BRANCH_LIST
    }
}
