/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.list

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.common.EventBus
import com.akvelon.bitbuckler.common.model.EventNames
import com.akvelon.bitbuckler.databinding.FragmentRepositoryListBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.ui.ads.AdsPresenter
import com.akvelon.bitbuckler.ui.ads.AdsView
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.repository.list.adapter.RepositoryListAdapter
import com.akvelon.bitbuckler.ui.screen.repository.list.adapter.RepositoryListSearchResultAdapter
import com.akvelon.bitbuckler.ui.screen.repository.list.adapter.RepositoryListSearchResultAdapter.Companion.CALCULATED_ITEM_HEIGHT
import com.akvelon.bitbuckler.ui.view.ads.BitbucklerAdsView
import com.akvelon.bitbuckler.ui.view.ads.customizeAndAddAdView
import com.akvelon.bitbuckler.ui.view.dpToPixel
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class RepositoryListFragment :
    MvpAppCompatFragment(R.layout.fragment_repository_list),
    RepositoryListView,
    AdsView,
    BackButtonListener {

    private lateinit var adapter: RepositoryListAdapter

    private lateinit var searchAdapter: RepositoryListSearchResultAdapter

    private val binding by viewBinding(FragmentRepositoryListBinding::bind)

    val presenter: RepositoryListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(WORKSPACE_ARG),
                argument(PROJECT_ARG)
            )
        }
    }

    private val adsPresenter: AdsPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = RepositoryListAdapter(
            presenter::onRepositoryClick,
            presenter::onRepositoryStateUpdated,
            presenter::loadNextRepositoriesPage
        )


        searchAdapter = RepositoryListSearchResultAdapter(
            clickListener = { repo ->
                presenter.onRepositoryClick(repo, true)
            }
        )

        adsPresenter.requestLoadingAds()
    }

    override fun onResume() {
        super.onResume()
        presenter.refreshRepositories()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recyclerViewRepositories.apply {
                setHasFixedSize(true)
                adapter = this@RepositoryListFragment.adapter
                addEndlessOnScrollListener { presenter.loadNextRepositoriesPage() }
            }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.forceUpdate() }
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshRepositories() }
            errorView.retry.setOnClickListener { presenter.refreshRepositories() }

            toolbar.setNavigationOnClickListener { onBackPressed() }

            searchIcon.setOnClickListener {
                presenter.onSearchClicked()
            }
        }

        lifecycleScope.launch {
            EventBus
                .subscribeByNames(EventNames.TAB_ACTIVITY_TAPPED, EventNames.TAB_PROFILE_TAPPED)
                .collect {
                    delay(50)
                    withContext(Dispatchers.Main) {
                        closeSearchView()
                    }
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

    override fun showError(show: Boolean) {
        binding.errorView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showLimitTrackedRepoError() {
        requireContext().showTrackLimitError(requireActivity())
    }

    override fun showEmptyView(show: Boolean) {
        binding.emptyListView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showErrorMessage(error: Throwable) {
        binding.recyclerViewRepositories.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.recyclerViewRepositories.post {
            adapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.recyclerViewRepositories.post {
            adapter.showProgressError(show)
        }
    }

    override fun showRepositories(
        show: Boolean,
        repositories: List<Repository>,
        scrollToTop: Boolean,
    ) {
        binding.recyclerViewRepositories.setVisible(show)
        adapter.setData(repositories)
        if (scrollToTop) {
            binding.recyclerViewRepositories.scrollToPosition(0)
        }
    }

    override fun expandSearchView() {
        with(binding) {
            searchInputView.isVisible = true
            searchInputView.requestFocus()
            searchInputView.showKeyboard()
            presenter.searchByTextQuery("")
            searchInputView.addTextChangedListener { text ->
                val queryString = text.toString()
                presenter.searchByTextQuery(queryString)
            }
            searchInputView.text.clear()
            toolbar.title = ""
            searchIcon.setImageResource(R.drawable.ic_close)
            searchIcon.setTint(R.color.graySmoke)
            searchIcon.setOnClickListener {
                presenter.onSearchClicked()
            }
            dimForeground.isVisible = true
            dimForeground.setOnClickListener {
                closeSearchView()
            }
        }
    }

    override fun closeSearchView() {
        try {
            with(binding) {
                binding.searchReposList.isVisible = false
                searchInputView.isVisible = false
                root.hideKeyboard()
                searchInputView.clearFocus()
                toolbar.title = requireContext().getString(R.string.repository_list_title)
                searchIcon.setImageResource(R.drawable.ic_search)
                searchIcon.setTint(R.color.blue)
                searchIcon.setOnClickListener {
                    presenter.onSearchClicked()
                }
                dimForeground.isVisible = false
                dimForeground.setOnClickListener(null)
            }
        } catch (ignore: IllegalStateException) {

        }
    }

    override fun onSearchResult(repos: List<Repository>) {
        binding.searchReposList.isVisible = true
        binding.searchReposList.adapter = searchAdapter
        searchAdapter.setData(repos)
        val params = binding.dimForeground.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(
            0,
            repos.size * CALCULATED_ITEM_HEIGHT.dpToPixel(requireContext().resources),
            0,
            0
        )
        binding.dimForeground.layoutParams = params
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
        const val PROJECT_ARG = "project_arg"

        fun newInstance(workspace: Workspace, project: Project) = RepositoryListFragment().apply {
            arguments = bundleOf(
                WORKSPACE_ARG to workspace,
                PROJECT_ARG to project
            )
        }
    }
}
