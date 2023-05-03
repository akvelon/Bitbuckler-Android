/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.commit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.commitNow
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentCommitDetailsBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.args.CommentsArgs
import com.akvelon.bitbuckler.model.entity.args.CommitDetailsArgs
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.comment.CommentListHolder
import com.akvelon.bitbuckler.ui.screen.comment.list.CommentListFragment
import com.akvelon.bitbuckler.ui.state.screen.CommitDetails
import com.github.terrakok.cicerone.Router
import io.noties.markwon.Markwon
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class CommitDetailsFragment :
    MvpAppCompatFragment(R.layout.fragment_commit_details),
    CommitDetailsView,
    RouterProvider,
    CommentListHolder,
    BackButtonListener {

    private val binding by viewBinding(FragmentCommitDetailsBinding::bind)

    override val router: Router
        get() = (parentFragment as RouterProvider).router

    val presenter: CommitDetailsPresenter by moxyPresenter {
        get {
            parametersOf(
                router,
                argument(COMMIT_DETAILS_SCOPE_ARG)
            )
        }
    }

    private val markwon: Markwon by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { refreshCommitDetails() }
            }

            errorView.retry.setOnClickListener { refreshCommitDetails() }
        }
    }

    override fun hideBottomNav() {
        (parentFragment as BottomNavListener).setBottomNavVisibility(false)
    }

    override fun showEmptyProgress(show: Boolean) {
        with(binding) {
            if (show) {
                skeletonLayout.showSkeleton()
            } else {
                skeletonLayout.showOriginal()
            }
        }
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showData(commitDetails: CommitDetails) {
        showCommit(commitDetails.commit)
    }

    override fun showError(error: Throwable) {
        binding.swipeToRefresh.isRefreshing = false
        requireView().snackError(error)
    }

    override fun showError(message: String) {
        binding.swipeToRefresh.isRefreshing = false
        requireView().snack(message)
    }

    override fun showComments() {
        childFragmentManager.commitNow {
            add(
                R.id.commentsContainer,
                CommentListFragment.newInstance(
                    CommentsArgs.fromCommitDetailsArgs(presenter.commitDetailsArgs)
                )
            )
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

    override fun showFullScreenProgress(show: Boolean) {
        binding.fullScreenProgress.root.setVisible(show)
    }

    override fun showDeleteCommentProgress(show: Boolean) {
        showFullScreenProgress(show)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    private fun refreshCommitDetails() {
        (childFragmentManager.fragments.first() as? CommentListFragment)?.refreshComments()
        presenter.refreshCommitDetails()
    }

    private fun showCommit(commit: Commit) = with(binding) {
        commit.author.user?.let {
            avatar.loadCircle(it.links.avatar.href)
            author.text = it.displayName
        } ?: run {
            avatar.setImageResource(R.drawable.ic_avatar_placeholder)
            author.text = commit.author.raw
        }

        sourceBranch.text = presenter.commitDetailsArgs.branch

        if (presenter.commitDetailsArgs.branch == null) {
            branchArrow.hide()
            sourceBranch.hide()
        }
        commitHash.text = commit.hash.take(COMMIT_HASH_LENGTH)

        updatedOn.text = updatedOn.getString(
            R.string.created,
            commit.date.timeAgo(requireContext())
        )

        toFiles.setOnClickListener { presenter.onFilesClick(commit.hash) }

        showMessage(commit.summary)
    }

    private fun showMessage(content: Content?) = with(binding.commitMessage) {
        if (content.isNullOrEmpty()) {
            text = getString(R.string.no_description)
            setTextAppearance(R.style.TextAppearance_AppCompat_Small)
            setItalicTypeface()
        } else {
            content?.let { markwon.setMarkdown(this, it.raw) }
        }
    }

    companion object {
        const val COMMIT_DETAILS_SCOPE_ARG = "commit_details_scope_arg"

        private const val COMMIT_HASH_LENGTH = 7

        fun newInstance(commitDetailsArgs: CommitDetailsArgs) = CommitDetailsFragment().apply {
            arguments = bundleOf(
                COMMIT_DETAILS_SCOPE_ARG to commitDetailsArgs
            )
        }
    }
}
