/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 20 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.workspace

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.common.EventBus
import com.akvelon.bitbuckler.common.model.EventNames
import com.akvelon.bitbuckler.databinding.FragmentWorkspaceListBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.setPrimaryColorScheme
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository
import com.akvelon.bitbuckler.ui.ads.AdsPresenter
import com.akvelon.bitbuckler.ui.ads.AdsView
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.workspace.adapter.RecentRepositoryListAdapter
import com.akvelon.bitbuckler.ui.screen.workspace.adapter.WorkspaceListAdapter
import com.akvelon.bitbuckler.ui.view.ads.BitbucklerAdsView
import com.akvelon.bitbuckler.ui.view.ads.customizeAndAddAdView
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class WorkspaceListFragment :
    MvpAppCompatFragment(R.layout.fragment_workspace_list),
    WorkspaceListView,
    AdsView,
    BackButtonListener {

    private lateinit var workspaceAdapter: WorkspaceListAdapter

    private lateinit var recentAdapter: RecentRepositoryListAdapter

    private val binding by viewBinding(FragmentWorkspaceListBinding::bind)

    val presenter: WorkspaceListPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    private val adsPresenter: AdsPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recentAdapter = RecentRepositoryListAdapter(
            presenter::onRepositoryClick,
            presenter::onRemoveClick
        )

        workspaceAdapter = WorkspaceListAdapter(
            presenter::onWorkspaceClick,
            presenter::loadNextWorkspacesPage
        )

        adsPresenter.requestLoadingAds()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recentRepositories.recyclerViewRepositoryCards.apply {
                setHasFixedSize(true)
                adapter = recentAdapter
            }

            recyclerViewWorkspaces.apply {
                setHasFixedSize(true)
                adapter = workspaceAdapter
                addEndlessOnScrollListener { presenter.loadNextWorkspacesPage() }
            }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshWorkspaces() }
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshWorkspaces() }
            errorView.retry.setOnClickListener { presenter.refreshWorkspaces() }
        }

        lifecycleScope.launch {
            EventBus.subscribeByName(EventNames.WORKSPACE_REOPENED)
                .collect { presenter.refreshWorkspaces() }
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.showRecentRepositories()
    }

    override fun showEmptyProgress(show: Boolean) {
        binding.progressEmptyListView.root.setVisible(show)

        binding.swipeToRefresh.apply {
            setVisible(!show)
            post {
                isRefreshing = false
            }
        }
    }

    override fun setNoInternetConnectionError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot_cry)
            errorTitle.text = getString(R.string.no_internet_title)
            errorSubtitle.text = getString(R.string.no_internet_subtitle)
        }

    override fun setUnknownError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.error_list_title)
            errorSubtitle.text = getString(R.string.error_list_subtitle)
        }

    override fun showErrorView(show: Boolean) {
        binding.errorView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showEmptyView(show: Boolean) {
        binding.emptyListView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showErrorMessage(error: Throwable) {
        binding.recyclerViewWorkspaces.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.recyclerViewWorkspaces.post {
            workspaceAdapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.recyclerViewWorkspaces.post {
            workspaceAdapter.showProgressError(show)
        }
    }

    override fun showWorkspaces(show: Boolean, workspaces: List<Workspace>) {
        binding.recyclerViewWorkspaces.setVisible(show)

        workspaceAdapter.setData(workspaces)
    }

    override fun showRecentRepositories(
        repositories: List<RecentRepository>,
        hasDataChanged: Boolean
    ) {
        recentAdapter.setData(repositories)

        with(binding) {
            if (hasDataChanged && repositories.isEmpty()) {
                appbar.setExpanded(false, true)

                recentRepositories.recyclerViewRepositoryCards.setVisible(false)
                recentRepositories.toolbar.setVisible(false)
            } else {
                collapsingToolbar.setVisible(repositories.isNotEmpty())
                (recentRepositories.recyclerViewRepositoryCards.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(0, 0)
            }
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun loadAds(activate: Boolean) {
        if (activate) {
            BitbucklerAdsView(requireContext(), BitbucklerAdsView.ADMOB)
                .getCurrentAdView()
                .customizeAndAddAdView(binding.rootView)
        } else {
            binding.rootView.children.find { it is AdView }?.let {
                val adView = it as AdView
                adView.destroy()
                adView.isVisible = false
                binding.rootView.removeView(adView)
            }
        }
    }
}
