/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.common.EventBus
import com.akvelon.bitbuckler.common.model.EventNames
import com.akvelon.bitbuckler.common.model.EventNames.PR_DIALOG_CLOSED
import com.akvelon.bitbuckler.databinding.DialogActivityScreenBinding
import com.akvelon.bitbuckler.databinding.FragmentActivityListBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.DataState
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.ui.ads.AdsPresenter
import com.akvelon.bitbuckler.ui.ads.AdsView
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.activity.adapter.*
import com.akvelon.bitbuckler.ui.screen.activity.dialog.PrLinkPresenter
import com.akvelon.bitbuckler.ui.screen.activity.dialog.PrParseView
import com.akvelon.bitbuckler.ui.screen.repository.TrackingReposDialogFragment
import com.akvelon.bitbuckler.ui.view.ads.BitbucklerAdsView
import com.akvelon.bitbuckler.ui.view.ads.customizeAndAddAdView
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.SkeletonConfig
import com.faltenreich.skeletonlayout.applySkeleton
import com.github.terrakok.cicerone.Router
import com.google.android.gms.ads.AdView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class ActivityListFragment :
    MvpAppCompatFragment(R.layout.fragment_activity_list),
    ActivityListView,
    AdsView,
    RouterProvider,
    PrParseView,
    BackButtonListener {

    private val trackReposDialogFragment = TrackingReposDialogFragment()

    private lateinit var userPrsAdapter: UserPrsAdapter
    private lateinit var userIssuesAdapter: UserIssuesAdapter
    private lateinit var trackedIssuesAdapter: TrackedIssuesAdapter
    private lateinit var trackedPrsAdapter: TrackedPrsAdapter
    private lateinit var normalPrAdapter: NormalPrsAdapter

    private var userIssuesLoader: Skeleton? = null
    private var userPrsLoader: Skeleton? = null
    private var trackedLoader: Skeleton? = null

    private val binding by viewBinding(FragmentActivityListBinding::bind)

    private val adsPresenter: AdsPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    val presenter: ActivityListPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    private val prLinkPresenter: PrLinkPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsPresenter.requestLoadingAds()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().intent.data?.let {
            prLinkPresenter.onPullRequestLink(it.toString(), isDeepLink = true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        setupRecyclerViews()
        with(binding) {
            ivTrack.setOnClickListener {
                presenter.navigateToTrackRepoScreen()
            }
            ivHelp.setOnClickListener {
                showActivityScreenInfoDialog()
            }
            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener {
                    presenter.refreshActivity(withLoader = false)
                    presenter.onRefreshRequestedByUser()
                }
            }
            emptyListView.refresh.setOnClickListener {
                presenter.refreshActivity(withLoader = false)
                presenter.onRefreshRequestedByUser()
            }
            errorView.retry.setOnClickListener {
                presenter.refreshActivity(withLoader = false)
            }
        }

        subscribeDataFlows()
        presenter.isShowTrackRepoDialog()
        setTabSelectedListener()
    }

    private fun showActivityScreenInfoDialog() {
        val dialogBinding = DialogActivityScreenBinding.inflate(layoutInflater, null, false)
        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.apply {
                if (!presenter.isPremiumActive())
                    setPositiveButton(R.string.subscriptions_get_premium_label_bold) { dialog, _ ->
                        presenter.startPremiumFlow()
                        dialog.dismiss()
                    }
            }.show()
    }

    override fun onDestroyView() {
        userIssuesLoader = null
        userPrsLoader = null
        trackedLoader = null
        super.onDestroyView()
    }

    override fun reflectPremiumStatus(isPremium: Boolean) {
        binding.grPremium.isVisible = isPremium
        binding.tvMyActivity.setText(if (isPremium) R.string.my_activity else R.string.tab_title_activity)

        binding.rvTracked.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = if (isPremium)
                trackedPrsAdapter
            else
                normalPrAdapter
            createTrackedListLoader()
        }

        if (isPremium) {
            reflectUserIssuesStateUpdate(presenter.userIssues.value)
            reflectUserPrsStateUpdate(presenter.userPrs.value)
            reflectTrackedListState(presenter.trackedPrs.value)
        }
    }

    private fun createTrackedListLoader() {
        trackedLoader = binding.rvTracked.applySkeleton(R.layout.item_tracked_pr_loader, 2, SkeletonConfig.default(requireContext()))
    }

    private fun setupListAdapter() {
        val windowWidth = getScreenWidth(requireActivity())
        userPrsAdapter = UserPrsAdapter(presenter::onPullRequestClick, windowWidth)
        userIssuesAdapter = UserIssuesAdapter(windowWidth, presenter::onIssueClick)
        trackedIssuesAdapter = TrackedIssuesAdapter(presenter::onIssueClick)
        trackedPrsAdapter = TrackedPrsAdapter(presenter::onPullRequestClick)
        normalPrAdapter = NormalPrsAdapter(presenter::onPullRequestClick)
    }

    private fun setupRecyclerViews() {
        with(binding) {
            rvUserPrs.apply {
                adapter = this@ActivityListFragment.userPrsAdapter
                layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.HORIZONTAL, false)
                userPrsLoader = applySkeleton(R.layout.item_user_pr_loader, 2, SkeletonConfig.default(requireContext()))
            }
            rvUserIssues.apply {
                adapter = userIssuesAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                userIssuesLoader = applySkeleton(R.layout.item_user_issue_loader, 2, SkeletonConfig.default(requireContext()))
            }
        }
    }

    private fun subscribeDataFlows() {
        presenter.userIssues
            .flowWithLifecycle(lifecycle)
            .filter { presenter.isPremiumActive() }
            .onEach(this::reflectUserIssuesStateUpdate)
            .launchIn(lifecycleScope)

        presenter.userPrs
            .flowWithLifecycle(lifecycle)
            .filter { presenter.isPremiumActive() }
            .onEach(this::reflectUserPrsStateUpdate)
            .launchIn(lifecycleScope)

        presenter.trackedIssues
            .flowWithLifecycle(lifecycle)
            .filter { presenter.isPremiumActive() }
            .filter {
                binding.issueTypeTabs.selectedTabPosition == 1
            }.onEach {
                reflectTrackedListState(it)
                if (it is DataState.Success)
                    trackedIssuesAdapter.setData(it.data)
            }.launchIn(lifecycleScope)

        presenter.trackedPrs
            .flowWithLifecycle(lifecycle)
            .filter { presenter.isPremiumActive() }
            .filter {
                binding.issueTypeTabs.selectedTabPosition == 0
            }.onEach {
                reflectTrackedListState(it)
                if (it is DataState.Success)
                    trackedPrsAdapter.setData(it.data)
            }.launchIn(lifecycleScope)

        presenter.allNormalUserPrs
            .flowWithLifecycle(lifecycle)
            .filter { !presenter.isPremiumActive() }
            .onEach(this::reflectNormalUserView)
            .launchIn(lifecycleScope)

        EventBus.subscribeByName(PR_DIALOG_CLOSED)
            .onEach {
                delay(50)
                binding.root.hideKeyboard()
            }.launchIn(lifecycleScope)

        EventBus.subscribeByName(EventNames.ACTIVITY_TRACK_REPO_CHANGED)
            .onEach {
                presenter.refreshActivity(withLoader = true)
            }.launchIn(lifecycleScope)
    }

    private fun reflectNormalUserView(state: DataState<List<TrackedPullRequest>>) {
        binding.errorView.root.isVisible = state is DataState.Error
        if (state is DataState.Error) {
            if (state.noNetwork)
                setNoInternetConnectionError()
            else
                setUnknownError()
        }
        binding.emptyListView.root.isVisible = state is DataState.Success && state.data.isEmpty()
        binding.progressEmptyListView.root.isVisible = state is DataState.Loading
        binding.swipeToRefresh.show()
        if (state is DataState.Success)
            normalPrAdapter.setData(state.data)
    }

    private fun reflectTrackedListState(it: DataState<List<*>>) {
        when (it) {
            is DataState.Error -> {
                trackedLoader?.showOriginal()
                binding.swipeToRefresh.isVisible = !it.noNetwork
                binding.errorView.root.isVisible = it.noNetwork
                if (it.noNetwork)
                    setNoInternetConnectionError()
                else
                    with(binding) {
                        ivTrackedListState.setImageResource(R.drawable.ic_robot)
                        ivTrackedListState.isVisible = true
                        tvTrackedListState.setText(R.string.error_list_subtitle)
                        tvTrackedListState.isVisible = true
                    }
            }
            is DataState.Loading -> {
                binding.swipeToRefresh.show()
                binding.errorView.root.hide()
                trackedLoader?.showSkeleton()
                binding.ivTrackedListState.isVisible = false
                binding.tvTrackedListState.isVisible = false
            }
            is DataState.Success -> {
                binding.swipeToRefresh.show()
                binding.errorView.root.hide()
                trackedLoader?.showOriginal()

                with(binding) {
                    if (it.data.isEmpty()) {
                        ivTrackedListState.setImageResource(R.drawable.ic_tree)
                        tvTrackedListState.setText(R.string.empty_list_title)
                    }
                    ivTrackedListState.isVisible = it.data.isEmpty()
                    tvTrackedListState.isVisible = it.data.isEmpty()
                }
            }
        }
    }

    private fun reflectUserPrsStateUpdate(it: DataState<List<TrackedPullRequest>>) {
        when (it) {
            is DataState.Error -> {
                userPrsLoader?.showOriginal()
                binding.swipeToRefresh.isVisible = !it.noNetwork
                binding.errorView.root.isVisible = it.noNetwork
                if (it.noNetwork)
                    setNoInternetConnectionError()
                else
                    with(binding) {
                        ivUserPrsListState.setImageResource(R.drawable.ic_robot)
                        ivUserPrsListState.isVisible = true
                        tvUserPrsListState.setText(R.string.error_list_subtitle)
                        tvUserPrsListState.isVisible = true
                    }
            }
            is DataState.Loading -> {
                binding.swipeToRefresh.show()
                binding.errorView.root.hide()
                userPrsLoader?.showSkeleton()
                binding.ivUserPrsListState.isVisible = false
                binding.tvUserPrsListState.isVisible = false
            }
            is DataState.Success -> {
                binding.swipeToRefresh.show()
                binding.errorView.root.hide()
                userPrsLoader?.showOriginal()

                val listHeight = resources.getDimensionPixelOffset(R.dimen.user_pr_list_height)
                (binding.rvUserPrs.layoutManager as GridLayoutManager).spanCount = if (it.data.size < 2) 1 else 2
                binding.rvUserPrs.updateLayoutParams {
                    height = if (it.data.size < 2) listHeight else listHeight * 2
                }

                userPrsAdapter.setData(it.data)

                with(binding) {
                    if (it.data.isEmpty()) {
                        ivUserPrsListState.setImageResource(R.drawable.ic_tree)
                        tvUserPrsListState.setText(R.string.empty_list_title)
                    }
                    ivUserPrsListState.isVisible = it.data.isEmpty()
                    tvUserPrsListState.isVisible = it.data.isEmpty()
                }
            }
        }
    }

    private fun reflectUserIssuesStateUpdate(it: DataState<List<TrackedIssue>>) {
        when (it) {
            is DataState.Error -> {
                userIssuesLoader?.showOriginal()
                binding.swipeToRefresh.isVisible = !it.noNetwork
                binding.errorView.root.isVisible = it.noNetwork
                if (it.noNetwork)
                    setNoInternetConnectionError()
                else
                    with(binding) {
                        ivUserIssuesListState.setImageResource(R.drawable.ic_robot)
                        ivUserIssuesListState.isVisible = true
                        tvUserIssuesListState.setText(R.string.error_list_subtitle)
                        tvUserIssuesListState.isVisible = true
                    }
            }
            is DataState.Loading -> {
                binding.swipeToRefresh.show()
                binding.errorView.root.hide()
                userIssuesLoader?.showSkeleton()
                binding.ivUserIssuesListState.isVisible = false
                binding.tvUserIssuesListState.isVisible = false
            }
            is DataState.Success -> {
                binding.swipeToRefresh.show()
                binding.errorView.root.hide()
                userIssuesLoader?.showOriginal()
                userIssuesAdapter.setData(it.data)

                with(binding) {
                    if (it.data.isEmpty()) {
                        ivUserIssuesListState.setImageResource(R.drawable.ic_tree)
                        tvUserIssuesListState.setText(R.string.empty_list_title)
                    }
                    ivUserIssuesListState.isVisible = it.data.isEmpty()
                    tvUserIssuesListState.isVisible = it.data.isEmpty()
                }
            }
        }
    }

    private fun setTabSelectedListener() {
        binding.issueTypeTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (presenter.isPremiumActive())
                    when (binding.issueTypeTabs.selectedTabPosition) {
                        0 -> {
                            val lastState = presenter.trackedPrs.value
                            binding.rvTracked.adapter = trackedPrsAdapter
                            createTrackedListLoader()
                            if (lastState is DataState.Success)
                                trackedPrsAdapter.setData(lastState.data)
                            reflectTrackedListState(lastState)
                        }
                        else -> {
                            val lastState = presenter.trackedIssues.value
                            binding.rvTracked.adapter = trackedIssuesAdapter
                            createTrackedListLoader()
                            if (lastState is DataState.Success)
                                trackedIssuesAdapter.setData(lastState.data)
                            reflectTrackedListState(lastState)
                        }
                    }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            presenter.refreshActivity(withLoader = false)
        }
    }

    private fun setUnknownError() =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.error_list_title)
            errorSubtitle.text = getString(R.string.error_list_subtitle)
        }

    private fun setNoInternetConnectionError() =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot_cry)
            errorTitle.text = getString(R.string.no_internet_title)
            errorSubtitle.text = getString(R.string.no_internet_subtitle)
        }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun showTrackReposPage(show: Boolean) {
        if (show) {
            trackReposDialogFragment.show(childFragmentManager, null)
        } else {
            if (trackReposDialogFragment.isAdded) {
                trackReposDialogFragment.dismiss()
            }
        }
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

    override val router: Router
        get() = (parentFragment as RouterProvider).router

    companion object {
        fun newInstance() = ActivityListFragment()
    }
}
