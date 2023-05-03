/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 15 December 2021
 */

package com.akvelon.bitbuckler.ui.screen.search

import android.net.Uri
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.SEARCH
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.args.SourceDetailsArgs
import com.akvelon.bitbuckler.model.entity.search.CodeSearchResult
import com.akvelon.bitbuckler.model.repository.WorkspaceRepository
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.pagination.Paginator
import com.akvelon.bitbuckler.ui.pagination.PaginatorRequestState
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
class SearchPresenter(
    router: Router,
    val workspace: Workspace,
    private val workspaceRepository: WorkspaceRepository
) : BasePresenter<SearchView>(router) {

    private var query = ""
    fun setQuery(query: String) {
        this.query = query
    }

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val results = getSearchResults(page)
                switchToUi { paginatorRequestState.success(results) }
            }
        },
        object : Paginator.ViewController<CodeSearchResult> {

            override fun showEmptyProgress(show: Boolean) =
                viewState.showEmptyProgress(show)

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                when (error) {
                    is SocketTimeoutException, is UnknownHostException ->
                        viewState.setNoInternetConnectionError(show)
                    is HttpException -> {
                        if (error.code() == NOT_FOUND_CODE) {
                            viewState.setSearchIsNotEnabledError(show)
                        }
                    }
                    is Throwable -> {
                        viewState.setUnknownError(show)
                        NonFatalError.log(error, screenName)
                    }
                }
                viewState.showErrorView(show)
            }

            override fun showEmptyView(show: Boolean) {
                viewState.showNoResultsView(show && query != "")
                viewState.showEmptyView(show)
            }

            override fun showData(show: Boolean, data: List<CodeSearchResult>, scrollToTop: Boolean) {
                viewState.showNoResultsView(data.isEmpty() || query == "")
                viewState.showResults(show, data)
            }

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

    var paginatorRequestState: PaginatorRequestState<PagedResponse<CodeSearchResult>> = paginator

    override fun onFirstViewAttach() {
        refreshResults()
    }

    fun onResultClick(result: CodeSearchResult) {
        router.navigateTo(
            Screen.sourceDetails(
                SourceDetailsArgs(
                    Slugs(workspace.slug, result.file.getRepo()),
                    result.file.getCommitHash(),
                    result.file.path
                )
            )
        )
    }

    fun onResultRepositoryClick(result: CodeSearchResult) {
        router.navigateTo(
            Screen.repositoryDetails(Slugs(workspace.slug, result.file.getRepo()))
        )
    }

    fun onEnablePressed() {
        val enableSearchUri = Uri.parse(enableSearchUriString)
        viewState.openSearchPage(enableSearchUri)
    }

    fun refreshResults() = paginator.refresh()

    fun loadNextResultsPage() = paginator.loadNewPage()

    override fun onDestroy() {
        paginator.release()
    }

    private suspend fun getSearchResults(page: String?) = workspaceRepository.search(
        workspace.slug,
        query,
        page
    )

    companion object {
        private const val screenName = SEARCH
        private const val NOT_FOUND_CODE = 404
        private const val enableSearchUriString = "https://bitbucket.org/search"
    }
}
