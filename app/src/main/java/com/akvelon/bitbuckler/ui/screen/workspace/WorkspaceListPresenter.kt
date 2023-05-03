/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 20 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.workspace

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.WORKSPACE_LIST
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository
import com.akvelon.bitbuckler.model.repository.RepoRepository
import com.akvelon.bitbuckler.model.repository.WorkspaceRepository
import com.akvelon.bitbuckler.model.repository.WorkspaceSizeRepository
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
class WorkspaceListPresenter(
    router: Router,
    private val analytics: AnalyticsProvider,
    private val workspaceRepository: WorkspaceRepository,
    private val repoRepository: RepoRepository,
    private val workspaceSizeRepository: WorkspaceSizeRepository
) : BasePresenter<WorkspaceListView>(router) {

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val response = getWorkspaces(page)
                switchToUi { paginatorRequestState.success(response) }
            }
        },
        object : Paginator.ViewController<Workspace> {

            override fun showEmptyProgress(show: Boolean) =
                viewState.showEmptyProgress(show)

            override fun showEmptyError(show: Boolean, error: Throwable?) =
                showError(show, error)

            override fun showEmptyView(show: Boolean) =
                viewState.showEmptyView(show)

            override fun showData(show: Boolean, data: List<Workspace>, scrollToTop: Boolean) =
                viewState.showWorkspaces(show, data)

            override fun showErrorMessage(error: Throwable) =
                viewState.showErrorMessage(error)

            override fun showRefreshProgress(show: Boolean) =
                viewState.showRefreshProgress(show)

            override fun showPageProgress(show: Boolean) =
                viewState.showPageProgress(show)

            override fun showPageProgressError(show: Boolean, error: Throwable?) {
                viewState.showPageProgressError(show)
            }
        }
    )

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Workspace>> = paginator

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.RepositoriesTab.RepositoriesTabScreen)

        refreshWorkspaces()
        showRecentRepositories()
    }

    fun refreshWorkspaces() {
        paginator.refresh()
    }

    fun loadNextWorkspacesPage() {
        paginator.loadNewPage()
    }

    fun onRemoveClick(recentRepository: RecentRepository) {
        analytics.report(AnalyticsEvent.RepositoriesTab.RepositoriesTabRemoveRecent)

        remove(recentRepository)
    }

    fun onRepositoryClick(recentRepository: RecentRepository) {
        analytics.report(AnalyticsEvent.RepositoriesTab.RepositoriesTabRecentClicked)

        val slugs = Slugs(recentRepository.workspaceSlug, recentRepository.slug)
        router.navigateTo(Screen.repositoryDetails(slugs))

        launchOnDefault {
            workspaceSizeRepository.handleWorkspaceSizeEvent(
                recentRepository.workspaceSlug,
                screenName
            )
        }
    }

    fun onWorkspaceClick(workspace: Workspace) {
        router.navigateTo(Screen.projectList(workspace))

        launchOnDefault {
            workspaceSizeRepository.handleWorkspaceSizeEvent(
                workspace.slug,
                screenName
            )
        }
    }

    override fun onDestroy() = paginator.release()

    private suspend fun getWorkspaces(page: String?) = workspaceRepository.getWorkspaces(page)

    fun showRecentRepositories() = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain { showError(true, throwable) }
        }
    ) {
        val repos = repoRepository.getAllRecent()
        switchToUi { viewState.showRecentRepositories(repos, false) }
    }

    private fun remove(recentRepository: RecentRepository) = launchOnDefault {
        repoRepository.deleteRecentRepo(recentRepository)
        val repos = repoRepository.getAllRecent()
        switchToUi { viewState.showRecentRepositories(repos, true) }
    }

    private fun showError(show: Boolean, error: Throwable?) {
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

    companion object {
        private const val screenName = WORKSPACE_LIST
    }
}
