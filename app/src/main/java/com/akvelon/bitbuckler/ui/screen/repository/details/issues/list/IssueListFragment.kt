/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 15 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.issues.list

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentIssuesBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueFilter
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.akvelon.bitbuckler.ui.screen.repository.details.issues.list.adapter.IssueListAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class IssueListFragment :
    MvpAppCompatFragment(R.layout.fragment_issues),
    IssueListView,
    SlideFragmentTransition,
    BackButtonListener {

    private lateinit var adapter: IssueListAdapter


    private val binding by viewBinding(FragmentIssuesBinding::bind)

    val presenter: IssueListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(SLUGS_ARG)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = IssueListAdapter(
            presenter::onIssueClick,
            presenter::loadNextIssuesPage
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.hideKeyboard()
                    presenter.onQueryTextSubmit(query.orEmpty())
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            searchView.setOnCloseListener {
                presenter.onQueryTextSubmit("")
                false
            }

            val lastSelectedTab = presenter.getLastIssueFilter().position
            issueTypeTabs.getTabAt(lastSelectedTab)?.select()

            issueTypeTabs.addSelectionListener { tab ->
                if (tab != null) {
                    val filter = IssueFilter.values().associateBy(IssueFilter::position)[tab]
                    if (filter != null)
                        presenter.onLastIssueStateChanged(filter)
                }
            }

            recyclerViewIssues.apply {
                setHasFixedSize(true)
                adapter = this@IssueListFragment.adapter
                addEndlessOnScrollListener { presenter.loadNextIssuesPage() }
            }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshIssues() }
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshIssues() }
            errorView.retry.setOnClickListener { presenter.refreshIssues() }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.refreshIssues()
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
        binding.recyclerViewIssues.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.recyclerViewIssues.post {
            adapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.recyclerViewIssues.post {
            adapter.showProgressError(show)
        }
    }

    override fun showIssues(show: Boolean, issues: List<Issue>) {
        binding.recyclerViewIssues.setVisible(show)

        adapter.setData(issues)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        const val SLUGS_ARG = "slugs_arg"

        fun newInstance(slugs: Slugs) = IssueListFragment().apply {
            arguments = bundleOf(
                SLUGS_ARG to slugs
            )
        }
    }
}
