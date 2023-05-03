/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.comment.list

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentCommentListBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.args.CommentsArgs
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.comment.CommentListHolder
import com.akvelon.bitbuckler.ui.screen.comment.adapter.CommentListAdapter
import com.akvelon.bitbuckler.ui.state.screen.CommentList
import com.akvelon.bitbuckler.ui.view.PullRequestActions
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import io.noties.markwon.Markwon
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class CommentListFragment :
    MvpAppCompatFragment(R.layout.fragment_comment_list),
    CommentListView {

    private val binding by viewBinding(FragmentCommentListBinding::bind)
    private var pullRequestActions: PullRequestActions? = null

    val presenter: CommentListPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(COMMENTS_ARGS)
            )
        }
    }

    private lateinit var commentsAdapter: CommentListAdapter

    private var commentsSkeleton: Skeleton? = null

    private var commentDeleteDialog: AlertDialog? = null

    private val markwon: Markwon by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        commentsAdapter = CommentListAdapter(
            markwon,
            presenter::addComment,
            presenter::editComment,
            presenter::showCommentDeleteDialog
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pullRequestActions = activity?.findViewById(R.id.pullRequestActions)

        binding.recyclerViewComments.adapter = commentsAdapter
        initListeners()
    }

    private fun initListeners() = with(binding) {
        commentText.setOnFocusChangeListener { _, _ ->
            if (commentText.text.isNullOrBlank() && presenter.parentComment == null) {
                toggleCommentActionButtonVisibility(R.drawable.ic_send)
            }
        }

        commentText.setOnClickListener { toggleCommentActionButtonVisibility(R.drawable.ic_send) }

        emptyError.retry.setOnClickListener { presenter.refreshComments() }

        ivCommentAction.setOnClickListener {
            root.hideKeyboard()
            presenter.sendComment(commentText.text.toString())
        }

        ivCloseReply.setOnClickListener {
            root.hideKeyboard()
            toggleCommentActionButtonVisibility()
        }
    }

    override fun showEmptyProgress(show: Boolean) {
        with(binding) {
            if (show) {
                skeletonLayout.showSkeleton()

                commentsSkeleton = recyclerViewComments.applySkeleton(R.layout.item_comment)
                commentsSkeleton?.showSkeleton()
            } else {
                skeletonLayout.showOriginal()
                commentsSkeleton?.showOriginal()
            }
        }
    }

    override fun showEmptyError(show: Boolean, error: Throwable?) {
        binding.emptyError.root.setVisible(show)
    }

    override fun showData(data: CommentList) {
        binding.accountAvatar.loadCircle(data.account.links.avatar.href)

        commentsAdapter.user = data.account
        updateComments(data.comments)
    }

    override fun showErrorMessage(error: Throwable) {
        binding.root.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.progressBar.setVisible(show)
    }

    override fun showCommentDeleteDialog(comment: Comment) {
        createCommentDeleteDialog(comment)
        commentDeleteDialog?.show()
    }

    override fun hideCommentDeleteDialog() {
        commentDeleteDialog?.dismiss()
    }

    override fun updateComments(comments: List<TreeNode<Comment>>) {
        commentsAdapter.setComments(comments)
    }

    override fun showFullScreenProgress(show: Boolean) {
        (parentFragment as? CommentListHolder)?.showDeleteCommentProgress(show)
    }

    override fun resetCommentField() = with(binding) {
        commentText.text = null
        toggleCommentActionButtonVisibility()
    }

    override fun setCommentField(content: String, resId: Int) = with(binding.commentText) {
        setReplyFlowVisibility(false)
        toggleCommentActionButtonVisibility(resId)
        setText(content)
        setSelection(length())
        focusAndShowKeyboard()
    }

    override fun showReplyToField(commentAuthor: String) {
        with(binding) {
            tvReplyTo.text = getString(R.string.replying_to, commentAuthor)
            setReplyFlowVisibility(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        commentDeleteDialog?.let {
            it.setOnDismissListener(null)
            it.dismiss()
        }
    }

    fun refreshComments() {
        presenter.refreshComments()
    }

    private fun createCommentDeleteDialog(comment: Comment) {
        commentDeleteDialog = AlertDialog.Builder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background
        )
            .setView(requireActivity().layoutInflater.inflate(R.layout.dialog_delete_comment, null))
            .setPositiveButton(
                R.string.pr_confirm_delete_comment_yes
            ) { _, _ ->
                presenter.deleteComment(comment)
            }
            .setNegativeButton(
                R.string.pr_confirm_delete_comment_no
            ) { _, _ ->
                presenter.hideCommentDeleteDialog()
            }
            .setOnDismissListener {
                presenter.hideCommentDeleteDialog()
            }
            .create()
    }

    private fun toggleCommentActionButtonVisibility(resId: Int? = null) = with(binding) {
        resId?.let {
            ivCommentAction.setImageResource(resId)
            setActionButtonVisibility(true)
            return
        }
        setActionButtonVisibility(false)
        setReplyFlowVisibility(false)
    }

    private fun setActionButtonVisibility(isVisible: Boolean) = with(binding) {
        ivCommentAction.setVisible(isVisible)
        divider.setVisible(isVisible)
        accountAvatar.setVisible(!isVisible)
        pullRequestActions?.setVisible(!isVisible)
    }

    private fun setReplyFlowVisibility(isVisible: Boolean) = with(binding) {
        tvReplyTo.setVisible(isVisible)
        ivCloseReply.setVisible(isVisible)
    }

    companion object {
        const val COMMENTS_ARGS = "comments_args"

        fun newInstance(commentsArgs: CommentsArgs) = CommentListFragment().apply {
            arguments = bundleOf(
                COMMENTS_ARGS to commentsArgs
            )
        }
    }
}
