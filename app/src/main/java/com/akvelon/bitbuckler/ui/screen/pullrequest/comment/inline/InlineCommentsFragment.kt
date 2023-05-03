/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest.comment.inline

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentInlineCommentsBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.args.InlineCommentsArgs
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.BinaryLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.akvelon.bitbuckler.ui.screen.comment.adapter.CommentListAdapter
import com.akvelon.bitbuckler.ui.state.screen.CommentList
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import io.noties.markwon.Markwon
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class InlineCommentsFragment :
    MvpAppCompatFragment(R.layout.fragment_inline_comments),
    InlineCommentsView,
    BackButtonListener,
    SlideFragmentTransition {

    private val binding by viewBinding(FragmentInlineCommentsBinding::bind)
    private var commentsSkeleton: Skeleton? = null

    val presenter: InlineCommentsPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(INLINE_COMMENTS_SCOPE_ARG)
            )
        }
    }

    private var commentsAdapter: CommentListAdapter? = null

    private var deleteCommentDialog: AlertDialog? = null

    private val markwon: Markwon by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            commentsTree.ivCommentAction.setOnClickListener { presenter.addComment(null, "") }
        }

        initListeners()
    }

    override fun hideBottomNav() {
        (parentFragment as BottomNavListener).setBottomNavVisibility(false)
    }

    override fun showInlineComments(
        file: FileDiff,
        line: LineDiff?,
        comments: List<TreeNode<Comment>>,
        account: User,
    ) {
        showFileHeader(file)
        showCodeLine(line)
        showComments(comments, account)
    }

    override fun showError(error: Throwable) {
        binding.container.snackError(error)
    }

    override fun showDeleteCommentDialog(comment: Comment) {
        createDeleteCommentDialogView(comment)
        deleteCommentDialog?.show()
    }

    override fun hideDeleteCommentDialog() {
        deleteCommentDialog?.dismiss()
    }

    override fun showDeleteCommentProgress(show: Boolean) {
        binding.deleteCommentProgress.root.setVisible(show)
    }

    override fun updateComments(comments: List<TreeNode<Comment>>) {
        commentsAdapter?.setComments(comments)
        binding.commentsTree.recyclerViewComments.adapter = commentsAdapter
    }

    override fun showProgress(show: Boolean) {
        with(binding.commentsTree) {
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
        binding.commentsTree.emptyError.root.setVisible(show)
    }

    override fun showData(data: CommentList) {
        binding.commentsTree.accountAvatar.loadCircle(data.account.links.avatar.href)

        commentsAdapter?.user = data.account
        updateComments(data.comments)
    }

    override fun showErrorMessage(error: Throwable) {
        binding.root.snackError(error)
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.commentsTree.progressBar.setVisible(show)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        deleteCommentDialog?.let {
            it.setOnDismissListener(null)
            it.dismiss()
        }
    }

    private fun showFileHeader(file: FileDiff) {
        binding.fileStatus.setImageResource(file.status.getStatusIcon())
        binding.filePath.text = file.getFormattingPath()
    }

    private fun showCodeLine(line: LineDiff?) =
        with(binding) {
            if (line == null) {
                lineView.hide()
                return
            }
            lineView.setBackgroundResource(line.getLineBackground())

            with(line.getMaxLineNumber().toString().length) {
                oldLineNumber.text = line.oldNumberToString(this)
                newLineNumber.text = line.newNumberToString(this)
            }

            prefix.setTextOrHide(line.getPrefix())

            if (line is BinaryLine) {
                content.text = content.getString(R.string.line_binary_content)
            } else {
                content.text = line.content
            }
        }

    private fun showComments(comments: List<TreeNode<Comment>>, account: User) =
        with(binding.commentsTree) {
            accountAvatar.loadCircle(account.links.avatar.href)

            commentsAdapter = CommentListAdapter(
                markwon,
                presenter::addComment,
                presenter::editComment,
                presenter::showDeleteCommentDialog,
                comments.toMutableList()
            ).apply {
                user = account
            }

            recyclerViewComments.adapter = commentsAdapter
        }

    private fun createDeleteCommentDialogView(comment: Comment) {
        deleteCommentDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.pr_confirm_delete_comment_title)
            .setMessage(R.string.pr_confirm_delete_comment_message)
            .setPositiveButton(
                R.string.pr_confirm_delete_comment_yes
            ) { _, _ ->
                presenter.deleteComment(comment)
            }
            .setNegativeButton(
                R.string.pr_confirm_delete_comment_no
            ) { _, _ ->
                presenter.hideDeleteCommentDialog()
            }
            .setOnDismissListener {
                presenter.hideDeleteCommentDialog()
            }
            .create()
    }

    private fun initListeners() = with(binding.commentsTree) {
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

    private fun toggleCommentActionButtonVisibility(resId: Int? = null) = with(binding.commentsTree) {
        resId?.let {
            ivCommentAction.setImageResource(resId)
            setActionButtonVisibility(true)
            return
        }
        setActionButtonVisibility(false)
        setReplyFlowVisibility(false)
    }

    private fun setActionButtonVisibility(isVisible: Boolean) = with(binding.commentsTree) {
        ivCommentAction.setVisible(isVisible)
    }

    private fun setReplyFlowVisibility(isVisible: Boolean) = with(binding.commentsTree) {
        tvReplyTo.setVisible(isVisible)
        ivCloseReply.setVisible(isVisible)
    }

    override fun setCommentField(content: String, resId: Int) = with(binding.commentsTree.commentText) {
        setReplyFlowVisibility(false)
        toggleCommentActionButtonVisibility(resId)
        setText(content)
        setSelection(length())
        focusAndShowKeyboard()
    }

    override fun showReplyToField(commentAuthor: String) {
        with(binding.commentsTree) {
            tvReplyTo.text = getString(R.string.replying_to, commentAuthor)
            setReplyFlowVisibility(true)
        }
    }

    override fun resetCommentField() = with(binding.commentsTree) {
        commentText.text = null
        toggleCommentActionButtonVisibility()
    }

    companion object {
        const val INLINE_COMMENTS_SCOPE_ARG = "inline_comments_scope_arg"

        const val INLINE_KEY_RESULT = "inline_result"

        fun newInstance(inlineCommentsArgs: InlineCommentsArgs) = InlineCommentsFragment().apply {
            arguments = bundleOf(
                INLINE_COMMENTS_SCOPE_ARG to inlineCommentsArgs
            )
        }
    }
}
