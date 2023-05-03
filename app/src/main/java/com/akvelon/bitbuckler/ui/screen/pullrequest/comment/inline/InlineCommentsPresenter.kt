/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest.comment.inline

import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.extension.createCommentsTreeList
import com.akvelon.bitbuckler.extension.removeDeletedComments
import com.akvelon.bitbuckler.extension.replaceElement
import com.akvelon.bitbuckler.model.entity.NewContent
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.args.InlineCommentsArgs
import com.akvelon.bitbuckler.model.entity.args.NewCommentArgs
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.comment.Inline
import com.akvelon.bitbuckler.model.entity.comment.NewComment
import com.akvelon.bitbuckler.model.entity.comment.Parent
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.CommentRepository
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.helper.MentionHelper
import com.akvelon.bitbuckler.ui.state.ScreenData
import com.akvelon.bitbuckler.ui.state.ScreenManagerRequestState
import com.akvelon.bitbuckler.ui.state.ScreenStateManager
import com.akvelon.bitbuckler.ui.state.screen.CommentList
import com.github.terrakok.cicerone.ResultListenerHandler
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState

@Suppress("TooManyFunctions")
@InjectViewState
class InlineCommentsPresenter(
    router: Router,
    private val inlineCommentsArgs: InlineCommentsArgs,
    private val accountRepository: AccountRepository,
    private val commentRepository: CommentRepository,
    private val mentionHelper: MentionHelper,
) : BasePresenter<InlineCommentsView>(router) {

    private val comments = mutableListOf<TreeNode<Comment>>()
    private var resultHandler: ResultListenerHandler? = null
    var parentComment: Comment? = null
    private val commentArgs = with(inlineCommentsArgs) {
        NewCommentArgs(
            commentsArgs,
            parentComment = parentComment,
            inline = Inline.fromDiff(file, line)
        )
    }

    private val stateManager = ScreenStateManager(
        {
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { stateCallback.failure(throwable) }
                }
            ) {
                val commentList = getCommentList()
                switchToUi { stateCallback.success(commentList) }
            }
        },
        object : ScreenStateManager.ViewController<CommentList> {
            override fun showEmptyProgress(show: Boolean) {
                viewState.showProgress(show)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                viewState.showEmptyError(show, error)
            }

            override fun showEmptyView(show: Boolean) {
            }

            override fun showData(data: CommentList) {
                viewState.showData(data)
            }

            override fun showErrorMessage(error: Throwable) {
                viewState.showErrorMessage(error)
            }

            override fun showRefreshProgress(show: Boolean) {
                viewState.showRefreshProgress(show)
            }
        }
    )

    private val stateCallback: ScreenManagerRequestState<CommentList> = stateManager

    override fun onFirstViewAttach() {

        viewState.hideBottomNav()

        getAccount()
    }

    fun showDeleteCommentDialog(comment: Comment) {
        viewState.showDeleteCommentDialog(comment)
    }

    fun hideDeleteCommentDialog() {
        viewState.hideDeleteCommentDialog()
    }

    fun addComment(parentComment: Comment?, commentAuthor: String) {
        this.parentComment = parentComment
        viewState.setCommentField("", R.drawable.ic_reply)
        viewState.showReplyToField(commentAuthor)
    }

    fun editComment(comment: Comment) {
        commentArgs.editableComment = comment
        viewState.setCommentField(comment.content.raw, R.drawable.ic_edit)
    }

    fun sendComment(content: String) = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain {
                viewState.showProgress(false)
                viewState.showError(throwable)
            }
        }
    ) {
        val parentId = this@InlineCommentsPresenter.parentComment?.id

        val comment = if (parentId == null) {
            NewComment(
                NewContent(content)
            )
        } else {
            NewComment(
                NewContent(content),
                Parent(parentId)
            )
        }

        commentArgs.inline?.let { comment.inline = it }

        switchToUi { viewState.showProgress(true) }
        val newComment = send(comment)
        if (commentArgs.editableComment == null) addNewComment(newComment, parentComment)
        else updateComment(newComment)
        commentArgs.editableComment = null
        parentComment = null

        mentionHelper.setMentions(newComment.content.raw)
        switchToUi {
            viewState.showProgress(false)
            viewState.resetCommentField()
            refreshComments()
        }
    }

    private suspend fun send(comment: NewComment) =
        with(commentArgs) {
            editableComment?.let {
                val commentToEdit = NewComment(content = comment.content, parent = null)
                return@with  commentRepository.editComment(
                    commentScope.slugs.workspace,
                    commentScope.slugs.repository,
                    commentScope.scope,
                    commentScope.id,
                    it.id,
                    commentToEdit
                )
            }

            return commentRepository.addNewComment(
                commentScope.slugs.workspace,
                commentScope.slugs.repository,
                commentScope.scope,
                commentScope.id,
                comment
            )
        }

    fun refreshComments() {
        stateManager.refresh()
    }

    private suspend fun getCommentList(): ScreenData<CommentList> {
        val newComments = getComments()
        val account = accountRepository.getCurrentAccount()
        comments.apply {
            clear()
            addAll(newComments)
        }

        return ScreenData(CommentList(flattenComments(), account))
    }

    private suspend fun getComments(): List<TreeNode<Comment>> {
        val commentsList = getAllComments()
        return commentsList.map { comment ->
            mentionHelper.setMentions(comment.content.raw).let {
                if (comment.content.raw != it) {
                    comment.updateComment(
                        comment.content.setMentionedRaw(it)
                    )
                } else {
                    comment
                }
            }
        }.sortedBy { comment -> comment.createdOn }
            .toMutableList()
            .createCommentsTreeList()
    }

    private suspend fun getAllComments(page: String? = null): List<Comment> {
        with(inlineCommentsArgs.commentsArgs) {
            val comments = mutableListOf<Comment>()
            val response = commentRepository.getInlineComments(
                slugs.workspace,
                slugs.repository,
                scope,
                id,
                page
            )

            comments.addAll(response.values)
            var nextPage = response.nextPage
            while (nextPage != null) {
                val tmp = commentRepository.getInlineComments(
                    slugs.workspace,
                    slugs.repository,
                    scope,
                    id,
                    nextPage
                )
                comments.addAll(tmp.values)
                nextPage = tmp.nextPage
            }
            return comments
        }
    }

    fun deleteComment(comment: Comment) = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain {
                viewState.showError(throwable)
            }
        }
    ) {
        switchToUi { viewState.showDeleteCommentProgress(true) }
        with(inlineCommentsArgs.commentsArgs) {
            commentRepository.deleteComment(
                slugs.workspace,
                slugs.repository,
                scope,
                id,
                comment.id
            )
            val deletedComment = comment.copy().apply { deleted = true }
            updateComment(deletedComment)
            inlineCommentsArgs.line?.comments?.removeDeletedComments()
                ?: inlineCommentsArgs.file.comments.removeDeletedComments()
            val flattenedComments = flattenComments()
            switchToUi {
                viewState.showDeleteCommentProgress(false)
                viewState.updateComments(flattenedComments)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        resultHandler?.dispose()
    }

    override fun onBackPressed() {
        router.sendResult(InlineCommentsFragment.INLINE_KEY_RESULT, Unit)

        super.onBackPressed()
    }

    private fun addNewComment(newComment: Comment, parentComment: Comment?) {
        if (parentComment != null) {
            inlineCommentsArgs.comments.forEach { parent ->
                parent.findById(parentComment.id)?.let {
                    it.addChild(TreeNode(newComment.id, newComment, it.level + 1))
                    return
                }
            }
        } else {
            inlineCommentsArgs.line?.comments?.add(TreeNode(newComment.id, newComment))
                ?: inlineCommentsArgs.file.comments.add(TreeNode(newComment.id, newComment))
        }
    }

    private fun flattenComments(): MutableList<TreeNode<Comment>> {
        val result = mutableListOf<TreeNode<Comment>>()
        val comments = inlineCommentsArgs.line?.comments ?: inlineCommentsArgs.file.comments
        comments.forEach { comment ->
            result.addAll(comment.flatten())
        }

        return result
    }

    private fun updateComment(newComment: Comment) {
        if (newComment.parent != null) {
            inlineCommentsArgs.comments.forEach { comments ->
                comments.findById(newComment.parent.id)?.replaceChild(newComment.id, newComment)
            }
        } else {
            inlineCommentsArgs.comments.find { parent ->
                parent.id == newComment.id
            }?.let {
                val newNode = it.copy()
                newNode.element = newComment
                if (inlineCommentsArgs.line != null) {
                    inlineCommentsArgs.line.comments.replaceElement(it, newNode)
                } else {
                    inlineCommentsArgs.file.comments.replaceElement(it, newNode)
                }
            }
        }
    }

    private fun getAccount() = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain { viewState.showError(throwable) }
        }
    ) {
        val user = accountRepository.getCurrentAccount()
        val comments = flattenComments()
        switchToUi {
            viewState.showInlineComments(
                inlineCommentsArgs.file,
                inlineCommentsArgs.line,
                comments = comments,
                account = user
            )
        }
    }
}
