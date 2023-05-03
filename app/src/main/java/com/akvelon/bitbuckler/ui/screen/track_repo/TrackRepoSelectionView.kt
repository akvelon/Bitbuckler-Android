package com.akvelon.bitbuckler.ui.screen.track_repo

import com.akvelon.bitbuckler.model.entity.LoadableDataState
import com.akvelon.bitbuckler.model.entity.repository.Repository
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface TrackRepoSelectionView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setWorkspaces(workspaces: List<WorkspaceExtended>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateTrackedRepoNumber(countTracked: Int, dataState: LoadableDataState)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateDataState(dataState: LoadableDataState)

    @StateStrategyType(SkipStrategy::class)
    fun updateWorkspace(workspace: WorkspaceExtended)

    @StateStrategyType(SkipStrategy::class)
    fun updateRepository(workspaceExtended: WorkspaceExtended, repo: Repository)

    @StateStrategyType(SkipStrategy::class)
    fun showTrackLimitError()
}