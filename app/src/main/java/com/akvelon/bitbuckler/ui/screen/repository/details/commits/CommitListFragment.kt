/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.commits

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.DialogSelectBranchBinding
import com.akvelon.bitbuckler.databinding.FragmentCommitListBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.setPrimaryColorScheme
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.model.entity.args.CommitsArgs
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.repository.details.commits.adapter.CommitListAdapter
import com.akvelon.bitbuckler.ui.screen.repository.details.source.list.adapter.SelectBranchAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class CommitListFragment :
    MvpAppCompatFragment(R.layout.fragment_commit_list),
    CommitListView,
    BackButtonListener {

    private val binding by viewBinding(FragmentCommitListBinding::bind)

    val presenter: CommitListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(COMMITS_ARGS)
            )
        }
    }

    private lateinit var adapter: CommitListAdapter

    private var branchDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = CommitListAdapter(
            presenter::onCommitClick,
            presenter::loadNextCommitsPage
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshCommits() }
            }

            recyclerViewCommits.apply {
                addEndlessOnScrollListener { presenter.loadNextCommitsPage() }
                adapter = this@CommitListFragment.adapter
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshCommits() }
            errorView.retry.setOnClickListener { presenter.refreshCommits() }
            fullScreenProgress.root.setOnTouchListener { _, _ -> true }

            toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.selectBranch -> presenter.onSelectBranchClick()
                    R.id.selectTag -> presenter.onSelectTagClick()
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

    override fun showBranchName(branchName: String) {
        binding.toolbar.subtitle = getString(R.string.source_branch_name, branchName)
    }

    override fun showBranchDialogProgress(show: Boolean) {
        binding.fullScreenProgress.root.setVisible(show)
    }

    override fun showBranchDialog(branches: List<Branch>, isTags: Boolean) {
        createBranchDialog(branches, isTags)
        branchDialog?.show()
    }

    override fun hideBranchDialog() {
        branchDialog?.dismiss()
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    private fun createBranchDialog(branches: List<Branch>, isTags: Boolean) {
        val dialogBinding = DialogSelectBranchBinding.bind(binding.root.inflate(R.layout.dialog_select_branch))

        with(dialogBinding) {
            dialogTitle.text = if (isTags) {
                getString(R.string.source_menu_select_tag)
            } else {
                getString(R.string.source_menu_select_branch)
            }

            recyclerViewBranch.apply {
                setHasFixedSize(true)
                adapter = SelectBranchAdapter(branches, presenter::onBranchClick)
            }
        }

        branchDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setOnDismissListener {
                presenter.hideBranchDialog()
            }
            .create()
    }

    companion object {
        const val COMMITS_ARGS = "commits_args"

        fun newInstance(commitsArgs: CommitsArgs) = CommitListFragment().apply {
            arguments = bundleOf(
                COMMITS_ARGS to commitsArgs
            )
        }
    }
}
