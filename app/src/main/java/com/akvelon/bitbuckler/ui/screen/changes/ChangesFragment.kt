/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 January 2021
 */

package com.akvelon.bitbuckler.ui.screen.changes

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentChangesBinding
import com.akvelon.bitbuckler.databinding.ItemChangesBodyBinding
import com.akvelon.bitbuckler.extension.anchor
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.collapse
import com.akvelon.bitbuckler.extension.isAnchoredOrExpanded
import com.akvelon.bitbuckler.extension.setPrimaryColorScheme
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.model.entity.args.ChangesArgs
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileStat
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.akvelon.bitbuckler.ui.screen.changes.adapter.ChangesAdapter
import com.akvelon.bitbuckler.ui.screen.changes.adapter.FilesAdapter
import com.akvelon.bitbuckler.ui.state.screen.DiffDetails
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class ChangesFragment :
    MvpAppCompatFragment(R.layout.fragment_changes),
    ChangesView,
    BackButtonListener,
    SlideFragmentTransition {

    private val fragmentBinding by viewBinding(FragmentChangesBinding::bind)

    val presenter: ChangesPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(CHANGES_SCOPE_ARG)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(fragmentBinding) {
            toolbar.setNavigationOnClickListener { presenter.onBackPressed() }

            slidingPanel.setFadeOnClickListener {
                slidingPanel.collapse()
            }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshDiffs() }
            }

            recyclerViewChanges.apply {
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0) {
                            filesButton.shrink()
                        } else {
                            filesButton.extend()
                        }
                    }
                })
            }
            filesBottomSheet.recyclerViewFiles.apply {
                setHasFixedSize(true)
            }

            filesButton.setOnClickListener {
                slidingPanel.anchor()
                presenter.onFilesButtonClicked()
            }

            errorView.retry.setOnClickListener { presenter.refreshDiffs() }
            emptyDiff.refresh.setOnClickListener { presenter.refreshDiffs() }
        }
    }

    override fun hideBottomNav() {
        (parentFragment as BottomNavListener).setBottomNavVisibility(false)
    }

    override fun showTitle(title: String) {
        fragmentBinding.toolbar.title = title
    }

    override fun showData(data: DiffDetails) {
        showChanges(data)
        showFiles(data.files)

        fragmentBinding.slidingPanel.show()
    }

    override fun showEmptyProgress(show: Boolean) {
        fragmentBinding.emptyProgress.root.setVisible(show)
    }

    override fun showRefreshProgress(show: Boolean) {
        fragmentBinding.swipeToRefresh.isRefreshing = show
    }

    override fun setNoInternetConnectionError(show: Boolean) =
        with(fragmentBinding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot_cry)
            errorTitle.text = getString(R.string.no_internet_title)
            errorSubtitle.text = getString(R.string.no_internet_subtitle)
        }

    override fun setUnknownError(show: Boolean) =
        with(fragmentBinding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.error_list_title)
            errorSubtitle.text = getString(R.string.error_list_subtitle)
        }

    override fun showErrorView(show: Boolean) {
        fragmentBinding.errorView.root.setVisible(show)
        fragmentBinding.swipeToRefresh.isEnabled = !show
    }

    override fun showEmptyView(show: Boolean) {
        fragmentBinding.emptyDiff.root.setVisible(show)
        fragmentBinding.swipeToRefresh.isEnabled = !show
    }

    override fun showError(error: Throwable) {
        fragmentBinding.swipeToRefresh.isRefreshing = false
        fragmentBinding.recyclerViewChanges.snackError(error)
    }

    override fun notifyLineOrFileChanged(filePosition: Int, linePosition: Int?) {
        if (linePosition == null) {
            fragmentBinding.recyclerViewChanges.adapter?.notifyItemChanged(filePosition * 2)
            return
        }

        (fragmentBinding.recyclerViewChanges.layoutManager as LinearLayoutManager).findViewByPosition(
            filePosition
        )
            ?.let {
                ItemChangesBodyBinding.bind(it).recyclerViewDiffs.adapter?.notifyItemChanged(
                    linePosition
                )
            }
    }

    override fun onBackPressed() {
        with(fragmentBinding) {
            if (slidingPanel.isAnchoredOrExpanded()) {
                slidingPanel.collapse()
            } else {
                presenter.onBackPressed()
            }
        }
    }

    private fun showChanges(data: DiffDetails) {
        fragmentBinding.recyclerViewChanges.adapter = ChangesAdapter(
            data,
            presenter::onCommentClick
        )
    }

    private fun showFiles(files: List<FileStat>) {
        fragmentBinding.filesBottomSheet.recyclerViewFiles.adapter =
            FilesAdapter(files, ::onFileClick)
    }

    private fun onFileClick(position: Int) {
        presenter.onFileClick()

        with(fragmentBinding) {
            slidingPanel.collapse()
            (recyclerViewChanges.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(position, 0)
        }
    }

    companion object {
        const val CHANGES_SCOPE_ARG = "changes_scope_arg"

        fun newInstance(changesArgs: ChangesArgs) = ChangesFragment().apply {
            arguments = bundleOf(
                CHANGES_SCOPE_ARG to changesArgs
            )
        }
    }
}
