/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 09 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.project.list

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.PROJECT_LIST
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.repository.ProjectRepository
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
class ProjectListPresenter(
    router: Router,
    val workspace: Workspace,
    private val projectRepository: ProjectRepository
) : BasePresenter<ProjectListView>(router) {

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val projects = getProjects(page)
                switchToUi { paginatorRequestState.success(projects) }
            }
        },
        object : Paginator.ViewController<Project> {

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

            override fun showData(show: Boolean, data: List<Project>, scrollToTop: Boolean) =
                viewState.showProjects(show, data, scrollToTop)

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

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Project>> = paginator

    override fun onFirstViewAttach() {
        refreshProjects()
    }

    fun refreshProjects() = paginator.refresh()

    fun loadNextProjectsPage() = paginator.loadNewPage()

    fun onProjectClick(project: Project) {
        router.navigateTo(Screen.repositoryList(workspace, project))
    }

    fun toSearchScreen() {
        router.navigateTo(
            Screen.search(workspace)
        )
    }

    override fun onDestroy() = paginator.release()

    private suspend fun getProjects(page: String?) = projectRepository.getProjects(
        workspace.slug,
        page
    )

    companion object {
        private const val screenName = PROJECT_LIST
    }
}
