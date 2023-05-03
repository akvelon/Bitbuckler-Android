package com.akvelon.bitbuckler.ui.screen.track_repo

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.common.EventBus
import com.akvelon.bitbuckler.common.model.Event
import com.akvelon.bitbuckler.common.model.EventNames
import com.akvelon.bitbuckler.databinding.FragmentTrackRepoBinding
import com.akvelon.bitbuckler.extension.showTrackLimitError
import com.akvelon.bitbuckler.model.entity.LoadableDataState
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.track_repo.adapter.ExpandableWorkspaceAdapter
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class TrackRepoSelectionFragment:
    MvpAppCompatFragment(R.layout.fragment_track_repo),
    TrackRepoSelectionView,
    BackButtonListener {

    private lateinit var trackAdapter: ExpandableWorkspaceAdapter
    private val binding by viewBinding(FragmentTrackRepoBinding::bind)

    val presenter: TrackRepoSelectionPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trackAdapter = ExpandableWorkspaceAdapter(
            presenter::onRepositoryStateUpdated,
            presenter::onWorkspaceExpanded
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.updateData()
        with(binding) {
            recyclerViewRepositories.apply {
                setHasFixedSize(true)
                adapter = trackAdapter
            }

            (recyclerViewRepositories.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            trackButton.setText(R.string.track_btn_no_selected_repos)
            trackButton.isEnabled = false
            trackButton.setOnClickListener {
                presenter.trackSelectedRepos()
                lifecycleScope.launch {
                    EventBus.pushEvent(Event(EventNames.ACTIVITY_TRACK_REPO_CHANGED))
                }
            }
            emptyListView.refresh.setOnClickListener { presenter.updateData() }
            errorView.retry.setOnClickListener { presenter.updateData() }

            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        fun newInstance() = TrackRepoSelectionFragment()
    }

    override fun updateTrackedRepoNumber(countTracked: Int, dataState: LoadableDataState) {
        if (dataState == LoadableDataState.LOADED_CHANGED) {
            binding.trackButton.text = resources.getString(R.string.track_btn_number_selected_repos, countTracked)
        } else {
            binding.trackButton.setText(R.string.track_btn_no_selected_repos)
        }
    }

    override fun updateDataState(dataState: LoadableDataState) {
        with (binding) {
            recyclerViewRepositories.isVisible = dataState == LoadableDataState.LOADED_UNCHANGED
                    || dataState == LoadableDataState.LOADED_CHANGED
            emptyListView.root.isVisible = dataState == LoadableDataState.NO_DATA
            errorView.root.isVisible = dataState == LoadableDataState.UNKNOWN_ERROR
                    || dataState == LoadableDataState.NETWORK_ERROR
            progressEmptyListView.root.isVisible = dataState == LoadableDataState.LOADING
            binding.trackButton.isEnabled = dataState == LoadableDataState.LOADED_CHANGED
        }
    }

    override fun updateWorkspace(workspace: WorkspaceExtended) {
        trackAdapter.updateWorkspace(workspace)
    }

    override fun updateRepository(workspaceExtended: WorkspaceExtended, repo: Repository) {
        trackAdapter.updateRepository(workspaceExtended, repo)
    }

    override fun setWorkspaces(workspaces: List<WorkspaceExtended>) {
        trackAdapter.setData(workspaces)
    }

    override fun showTrackLimitError() {
        requireContext().showTrackLimitError(requireActivity())
    }
}