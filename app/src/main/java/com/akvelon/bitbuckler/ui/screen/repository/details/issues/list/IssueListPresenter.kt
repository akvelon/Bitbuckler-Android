/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 05 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.issues.list

import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.args.IssueArgs
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueFilter
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.IssuesRepository
import com.akvelon.bitbuckler.model.repository.SettingsRepository
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.ISSUE_LIST
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
class IssueListPresenter(
    router: Router,
    private val slugs: Slugs,
    private val issuesRepository: IssuesRepository,
    private val settingsRepository: SettingsRepository,
    private val accountRepository: AccountRepository
) : BasePresenter<IssueListView>(router) {

    private var querySearchText = ""

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val response = getIssues(page)
                switchToUi { paginatorRequestState.success(response) }
            }
        },
        object : Paginator.ViewController<Issue> {

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

            override fun showData(show: Boolean, data: List<Issue>, scrollToTop: Boolean) =
                viewState.showIssues(show, data)

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

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Issue>> = paginator

    override fun onFirstViewAttach() {
    }

    fun refreshIssues() = paginator.refresh()

    fun loadNextIssuesPage() {
        paginator.loadNewPage()
    }

    fun getLastIssueFilter() = settingsRepository.getLastIssueFilter()

    fun onLastIssueStateChanged(filter: IssueFilter) {
        settingsRepository.setLastIssueFilter(filter)
        refreshIssues()
    }

    fun onIssueClick(issue: Issue) {
        router.navigateTo(
            Screen.issueDetails(
                IssueArgs(
                    slugs.workspace,
                    slugs.repository,
                    issue.id
                )
            )
        )
    }

    override fun onDestroy() = paginator.release()

     fun onQueryTextSubmit(text: String) {
        querySearchText = text
        refreshIssues()
    }

    private suspend fun getIssues(page: String?): PagedResponse<Issue> {
        val filter = getLastIssueFilter()

        return if (filter == IssueFilter.MINE) {
            with(accountRepository.getCurrentAccount()) {
                issuesRepository.getIssues(
                    slugs.workspace,
                    slugs.repository,
                    page,
                    filter,
                    accountId,
                    querySearchText = querySearchText
                )
            }
        } else issuesRepository.getIssues(
            slugs.workspace,
            slugs.repository,
            page,
            filter,
            querySearchText = querySearchText
        )
    }

    companion object {
        private const val screenName = ISSUE_LIST
    }
}
