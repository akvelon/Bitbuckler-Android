package com.akvelon.bitbuckler.ui.screen.track_repo

import com.akvelon.bitbuckler.domain.repo.CachedReposUseCase
import com.akvelon.bitbuckler.model.entity.LoadableDataState
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.model.entity.toRepository
import com.akvelon.bitbuckler.model.repository.ProjectRepository
import com.akvelon.bitbuckler.model.repository.TRACKED_REPOSITORY_LIMIT
import com.akvelon.bitbuckler.model.repository.WorkspaceRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.navigation.TabTags
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
class TrackRepoSelectionPresenter(
    router: Router,
    private val workspaceRepository: WorkspaceRepository,
    private val cachedReposUseCase: CachedReposUseCase,
    private val projectRepository: ProjectRepository,
    private val analytics: AnalyticsProvider
) : BasePresenter<TrackRepoSelectionView>(router) {

    private var workspaces = mutableMapOf<String, WorkspaceExtended>()
    private var startLoadingWorkspaces = mutableSetOf<String>()

    private var dataState = LoadableDataState.LOADING
    set(value) {
        field = value
        launchOnMain { viewState.updateDataState(dataState) }
    }

    override fun onFirstViewAttach() {
        launchOnMain { viewState.updateTrackedRepoNumber(0, dataState) }
        updateData()
        analytics.report(AnalyticsEvent.TrackingReposSetup.TrackReposSetupScreen)
    }

    fun updateData() = launchOnDefault(
        exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            dataState = when (throwable) {
                is SocketTimeoutException, is UnknownHostException -> {
                    LoadableDataState.NETWORK_ERROR
                }
                else -> {
                    LoadableDataState.UNKNOWN_ERROR
                }
            }
        }
    ) {
        dataState = LoadableDataState.LOADING
        val workspacesList = workspaceRepository
            .getAllWorkspaces()
            .map {
                val counter = workspaceRepository.getCounterForWorkspace(it.slug)
                WorkspaceExtended(
                    name = it.name,
                    slug = it.slug,
                    trackedNumberPreview = counter
                )
            }
        switchToUi {
            viewState.setWorkspaces(workspacesList)
            workspacesList.forEach { workspaces[it.slug] = it }

            dataState = if (workspaces.isEmpty()) {
                LoadableDataState.NO_DATA
            } else {
                LoadableDataState.LOADED_UNCHANGED
            }
        }
        switchToUi { viewState.updateTrackedRepoNumber(getTotalTracked(), dataState) }
    }

    fun onRepositoryStateUpdated(repository: Repository, newStatus: Boolean) {
        workspaces[repository.workspaceSlug]?.let { workspaceExtended ->
            workspaceExtended.repos?.get(repository.slug)?.let { repository ->
                if (newStatus && getTotalTracked() >= TRACKED_REPOSITORY_LIMIT) {
                    // We could not track more repos than te limit number. Show error.
                    viewState.showTrackLimitError()
                    analytics.report(AnalyticsEvent.TrackingReposSetup.TrackReposSetupNoMoreAlert)
                } else {
                    if (dataState == LoadableDataState.LOADED_UNCHANGED) {
                        dataState = LoadableDataState.LOADED_CHANGED
                    }
                    repository.isTracked = newStatus
                    viewState.updateRepository(workspaceExtended, repository)
                    viewState.updateTrackedRepoNumber(getTotalTracked(), dataState)
                }
            }
        }
    }

    fun trackSelectedRepos() = launchOnDefault {
        val allRepos = workspaces.values.map { it.repos?.values ?: emptyList() }.flatten()
        cachedReposUseCase.updateTrackedFlagForRepos(allRepos)
        switchToUi { router.backTo(Screen.account(TabTags.ACTIVITY)) }
        analytics.report(AnalyticsEvent.TrackingReposSetup.TrackReposSetupTrackConfirm)
    }

    fun onWorkspaceExpanded(workspace: WorkspaceExtended, isExpanded: Boolean) {
        if (isExpanded && !startLoadingWorkspaces.contains(workspace.slug)) {
            startLoadingWorkspaces.add(workspace.slug)
            launchOnIO { getRepositoriesForWorkspace(workspace) }
        }
    }

    private suspend fun getRepositoriesForWorkspace(workspace: WorkspaceExtended) {
        val listProjects = projectRepository.getAllProjects(workspace.slug)
        val reposMap = mutableMapOf<String, Repository>()
        listProjects.forEach { project ->
            reposMap.putAll(
                cachedReposUseCase
                    .updateIfNeeded(project.key, workspace.slug)
                    .map { it.toRepository() }
                    .associateBy { it.slug }
            )
        }
        workspace.repos = reposMap
        switchToUi {
            viewState.updateWorkspace(workspace)
            viewState.updateTrackedRepoNumber(getTotalTracked(), dataState)
        }
    }

    private fun getTotalTracked() = workspaces.values.sumOf { it.trackedNumber }

}