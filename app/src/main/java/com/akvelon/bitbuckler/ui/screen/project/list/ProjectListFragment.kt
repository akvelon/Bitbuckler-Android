/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 09 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.project.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentProjectListBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.ui.ads.AdsPresenter
import com.akvelon.bitbuckler.ui.ads.AdsView
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.project.list.adapter.ProjectListAdapter
import com.akvelon.bitbuckler.ui.view.ads.BitbucklerAdsView
import com.akvelon.bitbuckler.ui.view.ads.customizeAndAddAdView
import com.google.android.gms.ads.AdView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class ProjectListFragment :
    MvpAppCompatFragment(R.layout.fragment_project_list),
    ProjectListView,
    AdsView,
    BackButtonListener {

    private lateinit var adapter: ProjectListAdapter

    private val binding by viewBinding(FragmentProjectListBinding::bind)

    val presenter: ProjectListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(WORKSPACE_ARG)
            )
        }
    }

    private val adsPresenter: AdsPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ProjectListAdapter(
            presenter::onProjectClick,
            presenter::loadNextProjectsPage,
            presenter.workspace
        )

        adsPresenter.requestLoadingAds()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recyclerViewProjects.apply {
                setHasFixedSize(true)
                adapter = this@ProjectListFragment.adapter
                addEndlessOnScrollListener { presenter.loadNextProjectsPage() }
            }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshProjects() }
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshProjects() }
            errorView.retry.setOnClickListener { presenter.refreshProjects() }

            toolbar.setNavigationOnClickListener { onBackPressed() }
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.searchItem -> presenter.toSearchScreen()
                }
                true
            }
        }
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
        binding.recyclerViewProjects.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.recyclerViewProjects.post {
            adapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.recyclerViewProjects.post {
            adapter.showProgressError(show)
        }
    }

    override fun showProjects(show: Boolean, projects: List<Project>, scrollToTop: Boolean) {
        binding.recyclerViewProjects.setVisible(show)
        adapter.setData(projects)
        if(scrollToTop) {
            binding.recyclerViewProjects.scrollToPosition(0)
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

    companion object {
        const val WORKSPACE_ARG = "workspace_arg"

        fun newInstance(workspace: Workspace) = ProjectListFragment().apply {
            arguments = bundleOf(
                WORKSPACE_ARG to workspace
            )
        }
    }
}
