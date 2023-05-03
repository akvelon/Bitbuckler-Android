/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest.commits

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentPrCommitListBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.setPrimaryColorScheme
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.akvelon.bitbuckler.ui.screen.repository.details.commits.adapter.CommitListAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class PrCommitListFragment :
    MvpAppCompatFragment(R.layout.fragment_pr_commit_list),
    PrCommitListView,
    BackButtonListener,
    SlideFragmentTransition {

    private val binding by viewBinding(FragmentPrCommitListBinding::bind)

    val presenter: PrCommitListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(PR_COMMITS_ARGS)
            )
        }
    }

    private lateinit var adapter: CommitListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = CommitListAdapter(
            presenter::onCommitClick,
            presenter::loadNextCommitsPage
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshCommits() }
            }

            recyclerViewCommits.apply {
                addEndlessOnScrollListener { presenter.loadNextCommitsPage() }
                adapter = this@PrCommitListFragment.adapter
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshCommits() }
            errorView.retry.setOnClickListener { presenter.refreshCommits() }

            toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
        }
    }

    override fun hideBottomNav() {
        (parentFragment as BottomNavListener).setBottomNavVisibility(false)
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
        binding.recyclerViewCommits.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.recyclerViewCommits.post {
            adapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.recyclerViewCommits.post {
            adapter.showProgressError(show)
        }
    }

    override fun showCommits(show: Boolean, commits: List<Commit>) {
        binding.recyclerViewCommits.setVisible(show)

        adapter.setData(commits)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        const val PR_COMMITS_ARGS = "pr_commits_args"

        fun newInstance(pullRequestArgs: PullRequestArgs) = PrCommitListFragment().apply {
            arguments = bundleOf(
                PR_COMMITS_ARGS to pullRequestArgs
            )
        }
    }
}
