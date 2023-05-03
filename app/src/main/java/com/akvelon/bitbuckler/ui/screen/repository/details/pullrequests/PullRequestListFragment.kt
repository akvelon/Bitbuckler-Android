/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 15 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentPullRequestsBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.selected
import com.akvelon.bitbuckler.extension.setPrimaryColorScheme
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests.adapter.PullRequestListAdapter
import com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests.adapter.PullRequestStateAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class PullRequestListFragment :
    MvpAppCompatFragment(R.layout.fragment_pull_requests),
    PullRequestListView,
    SlideFragmentTransition,
    BackButtonListener {

    private lateinit var adapter: PullRequestListAdapter

    private lateinit var stateAdapter: PullRequestStateAdapter

    private val binding by viewBinding(FragmentPullRequestsBinding::bind)

    val presenter: PullRequestListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(SLUGS_ARG)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stateAdapter = PullRequestStateAdapter(
            PullRequestState.values().dropLast(1).toTypedArray()
        )

        adapter = PullRequestListAdapter(
            presenter::onPullRequestClick,
            presenter::loadNextPullRequestsPage
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            toolbar.setNavigationOnClickListener { onBackPressed() }

            spinner.apply {
                adapter = stateAdapter
                setSelection(presenter.getLastPullRequestState().position, false)

                post {
                    selected { presenter.onLastPrStateChanged(selectedItem as PullRequestState) }
                }
            }

            recyclerViewPullRequests.apply {
                setHasFixedSize(true)
                adapter = this@PullRequestListFragment.adapter
                addEndlessOnScrollListener { presenter.loadNextPullRequestsPage() }
            }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshPullRequests() }
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshPullRequests() }
            errorView.retry.setOnClickListener { presenter.refreshPullRequests() }
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
        binding.recyclerViewPullRequests.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.recyclerViewPullRequests.post {
            adapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.recyclerViewPullRequests.post {
            adapter.showProgressError(show)
        }
    }

    override fun showPullRequests(show: Boolean, pullRequests: List<PullRequest>) {
        binding.recyclerViewPullRequests.setVisible(show)

        adapter.setData(pullRequests)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        const val SLUGS_ARG = "slugs_arg"

        fun newInstance(slugs: Slugs) = PullRequestListFragment().apply {
            arguments = bundleOf(
                SLUGS_ARG to slugs
            )
        }
    }
}
