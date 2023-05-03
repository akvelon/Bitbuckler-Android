/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.list

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.REPOSITORY_LIST
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.*
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.domain.repo.CachedReposUseCase
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
class RepositoryListPresenter(
    router: Router,
    val workspace: Workspace,
    val project: Project,
    private val analytics: AnalyticsProvider,
    private val cachedReposUseCase: CachedReposUseCase
) : BasePresenter<RepositoryListView>(router) {

    var isForceUpdate = false

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val repos = if(isForceUpdate) {
                        isForceUpdate = false
                        cachedReposUseCase
                            .forceUpdate(
                                projectKey = project.key,
                                workspaceSlug = workspace.slug
                            )
                            .map { it.toRepository() }
                    } else {
                        cachedReposUseCase
                            .updateIfNeeded(
                                projectKey = project.key,
                                workspaceSlug = workspace.slug
                            )
                            .map { it.toRepository() }
                    }
                    val response = PagedResponse(
                        values = repos,
                        page = 1,
                        size = repos.size,
                        next = null,
                        pagelen = repos.size
                    )
                switchToUi { paginatorRequestState.success(response) }
            }
        },
        object : Paginator.ViewController<Repository> {

            override fun showEmptyProgress(show: Boolean) =
                viewState.showEmptyProgress(show)

            override fun showEmptyError(show: Boolean, error: Throwable?) =
                showError(show, error)

            override fun showEmptyView(show: Boolean) =
                viewState.showEmptyView(show)

            override fun showData(show: Boolean, data: List<Repository>, scrollToTop: Boolean) =
                viewState.showRepositories(show, data, scrollToTop)

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

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Repository>> = paginator

    private var isSearchExpanded = false

    fun onSearchClicked() {
        isSearchExpanded = !isSearchExpanded
        if (isSearchExpanded) {
            viewState.expandSearchView()
        } else {
            viewState.closeSearchView()
        }
    }

    fun searchByTextQuery(query: String) {
        if (query.isBlank()) {
            viewState.onSearchResult(emptyList())
        } else {
            launchOnDefault {
                val searchResult = cachedReposUseCase
                    .getReposBy(workspace.slug, project.key)
                    .filter { it.name.contains(query, ignoreCase = true) }
                switchToUi {
                    viewState.onSearchResult(searchResult.map { it.toRepository() })
                }
                analytics.report(AnalyticsEvent.RepositoriesList.RepositoriesListSearch)
            }
        }
    }

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.RepositoriesList.RepositoriesListScreen)

        refreshRepositories()
    }

    fun refreshRepositories() {
        paginator.reset()
        paginator.refresh()
    }

    fun forceUpdate() {
        isForceUpdate = true
        paginator.reset()
        paginator.refresh()
    }

    fun loadNextRepositoriesPage() {
        paginator.loadNewPage()
    }

    fun onRepositoryClick(repository: Repository, isFromSearch: Boolean = false) {
        router.navigateTo(Screen.repositoryDetails(Slugs(workspace.slug, repository.slug)))
        if (isFromSearch) {
            viewState.closeSearchView()
        }
    }

    fun onRepositoryStateUpdated(repository: Repository, state: Boolean) = launchOnIO {
        if (state) {
            if (!cachedReposUseCase.tryTrackRepository(repository.uuid)) {
                switchToUi {
                    refreshRepositories()
                    viewState.showLimitTrackedRepoError()
                }
            }
        } else { cachedReposUseCase.unTrackRepository(repository.uuid) }
        analytics.report(AnalyticsEvent.RepositoriesList.RepositoriesListTrackSetup)
    }

    override fun onDestroy() = paginator.release()

    private fun showError(show: Boolean, error: Throwable?) {
        when (error) {
            is SocketTimeoutException, is UnknownHostException ->
                viewState.setNoInternetConnectionError(show)
            is Throwable -> {
                viewState.setUnknownError(show)
                NonFatalError.log(error, screenName)
            }
        }
        viewState.showError(show)
    }

    companion object {
        private const val screenName = REPOSITORY_LIST
    }
}
