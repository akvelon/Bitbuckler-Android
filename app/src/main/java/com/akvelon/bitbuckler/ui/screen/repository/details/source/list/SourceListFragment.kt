/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 15 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.DialogSelectBranchBinding
import com.akvelon.bitbuckler.databinding.FragmentSourceListBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.setPrimaryColorScheme
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.model.entity.args.SourceArgs
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.source.Source
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.akvelon.bitbuckler.ui.screen.repository.details.source.list.adapter.SelectBranchAdapter
import com.akvelon.bitbuckler.ui.screen.repository.details.source.list.adapter.SourceListAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import java.io.IOException

@Suppress("TooManyFunctions")
class SourceListFragment :
    MvpAppCompatFragment(R.layout.fragment_source_list),
    SourceListView,
    SlideFragmentTransition,
    BackButtonListener {

    private val binding by viewBinding(FragmentSourceListBinding::bind)

    private lateinit var adapter: SourceListAdapter

    private var branchDialog: AlertDialog? = null

    val presenter: SourceListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(SOURCE_ARGS)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = SourceListAdapter(
            presenter::onSourceClick,
            presenter::toPreviousFolderClick,
            presenter::loadNextFilesPage
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshFiles() }
            }

            recyclerViewFiles.apply {
                itemAnimator = null
                adapter = this@SourceListFragment.adapter
                addEndlessOnScrollListener { presenter.loadNextFilesPage() }
            }

            emptyListView.refresh.setOnClickListener { presenter.refreshFiles() }
            errorView.retry.setOnClickListener { presenter.refreshFiles() }
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

    override fun showEmptyError(show: Boolean, error: Throwable?) {
        when (error) {
            is IOException -> showNoInternetConnectionError()
            is Throwable -> showUnknownError()
        }

        binding.errorView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showEmptyView(show: Boolean) {
        binding.emptyListView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showErrorMessage(error: Throwable) {
        binding.recyclerViewFiles.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showPageProgress(show: Boolean) {
        binding.recyclerViewFiles.post {
            adapter.showProgress(show)
        }
    }

    override fun showPageProgressError(show: Boolean) {
        binding.recyclerViewFiles.post {
            adapter.showProgressError(show)
        }
    }

    override fun showFiles(show: Boolean, files: List<Source>) {
        binding.recyclerViewFiles.setVisible(show)

        adapter.setData(files)
    }

    override fun showFolderName(folderName: String) = with(binding) {
        if (folderName.isNotEmpty()) {
            toolbar.title = folderName
        } else {
            toolbar.title = getString(R.string.source_title)
        }
    }

    override fun showBranchName(branchName: String) {
        binding.toolbar.subtitle = getString(R.string.source_branch_name, branchName)
    }

    override fun showBranchDialog(branches: List<Branch>, isTags: Boolean) {
        createBranchDialog(branches, isTags)
        branchDialog?.show()
    }

    override fun hideBranchDialog() {
        branchDialog?.dismiss()
    }

    override fun showBranchDialogProgress(show: Boolean) {
        binding.fullScreenProgress.root.setVisible(show)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        branchDialog?.let {
            it.setOnDismissListener(null)
            it.dismiss()
        }

        arguments?.putParcelable(SOURCE_ARGS, presenter.sourceArgs)
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

    private fun showNoInternetConnectionError() = with(binding.errorView) {
        errorImage?.setImageResource(R.drawable.ic_robot_cry)
        errorTitle.text = getString(R.string.no_internet_title)
        errorSubtitle.text = getString(R.string.no_internet_subtitle)
    }

    private fun showUnknownError() = with(binding.errorView) {
        errorImage?.setImageResource(R.drawable.ic_robot)
        errorTitle.text = getString(R.string.error_list_title)
        errorSubtitle.text = getString(R.string.error_list_subtitle)
    }

    companion object {
        const val SOURCE_ARGS = "source_args"

        fun newInstance(sourceArgs: SourceArgs) = SourceListFragment().apply {
            arguments = bundleOf(
                SOURCE_ARGS to sourceArgs
            )
        }
    }
}
