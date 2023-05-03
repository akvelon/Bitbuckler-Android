/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.comment.list

import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.extension.createCommentsTreeList
import com.akvelon.bitbuckler.extension.removeDeletedComments
import com.akvelon.bitbuckler.extension.replaceElement
import com.akvelon.bitbuckler.model.entity.NewContent
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.args.CommentScope.COMMITS
import com.akvelon.bitbuckler.model.entity.args.CommentScope.PULL_REQUESTS
import com.akvelon.bitbuckler.model.entity.args.CommentsArgs
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.comment.NewComment
import com.akvelon.bitbuckler.model.entity.comment.Parent
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.CommentRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
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
class CommentListPresenter(
    router: Router,
    private val commentsArgs: CommentsArgs,
    private val analytics: AnalyticsProvider,
    private val commentRepository: CommentRepository,
    private val accountRepository: AccountRepository,
    private val mentionHelper: MentionHelper
) : BasePresenter<CommentListView>(router) {

    private val comments = mutableListOf<TreeNode<Comment>>()

    private var resultHandler: ResultListenerHandler? = null

    private var editableComment: Comment? = null
    var parentComment: Comment? = null

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
                viewState.showEmptyProgress(show)
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
        refreshComments()
    }

    fun refreshComments() {
        stateManager.refresh()
    }

    fun addComment(parentComment: Comment?, commentAuthor: String) {
        this.parentComment = parentComment
        viewState.setCommentField("", R.drawable.ic_reply)
        viewState.showReplyToField(commentAuthor)
    }

    fun editComment(comment: Comment) {
        editableComment = comment
        viewState.setCommentField(comment.content.raw, R.drawable.ic_edit)
    }

    fun deleteComment(comment: Comment) {
        viewState.showFullScreenProgress(true)

        with(commentsArgs) {
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain {
                        viewState.showFullScreenProgress(false)
                        viewState.showErrorMessage(throwable)
                    }
                }
            ) {
                commentRepository.deleteComment(
                    slugs.workspace,
                    slugs.repository,
                    scope,
                    id,
                    comment.id
                )
                val deletedComment = comment.copy().apply { deleted = true }
                updateComment(deletedComment)
                comments.removeDeletedComments()

                switchToUi {
                    viewState.showFullScreenProgress(false)
                    viewState.updateComments(flattenComments())
                }
            }
        }
    }

    fun sendComment(content: String) = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain { viewState.showEmptyError(true, throwable) }
        }) {
        val parentId = parentComment?.id

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
        switchToUi {
            viewState.showEmptyProgress(true)
        }
        val newComment = send(comment)
        editableComment = null
        parentComment = null
        mentionHelper.setMentions(newComment.content.raw)
        switchToUi {
            viewState.showEmptyProgress(false)
            viewState.resetCommentField()
            refreshComments()
        }

        when (commentsArgs.scope) {
            PULL_REQUESTS -> analytics.report(AnalyticsEvent.PullRequest.PullRequestCommentSent)
            COMMITS -> analytics.report(AnalyticsEvent.CommitScreen.CommitCommentSent)
            else -> Unit
        }
    }

    private suspend fun send(comment: NewComment): Comment = with(commentsArgs) {
        editableComment?.let {
            return commentRepository.editComment(
                slugs.workspace,
                commentsArgs.slugs.repository,
                commentsArgs.scope,
                commentsArgs.id,
                it.id,
                comment
            )
        }
        return commentRepository.addNewComment(
            slugs.workspace,
            commentsArgs.slugs.repository,
            commentsArgs.scope,
            commentsArgs.id,
            comment
        )
    }

    fun showCommentDeleteDialog(comment: Comment) {
        viewState.showCommentDeleteDialog(comment)
    }

    fun hideCommentDeleteDialog() {
        viewState.hideCommentDeleteDialog()
    }

    override fun onDestroy() {
        super.onDestroy()

        resultHandler?.dispose()
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
            .filter {
                !it.content.raw.isNullOrEmpty()
            }
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
        with(commentsArgs) {
            val comments = mutableListOf<Comment>()
            val response = commentRepository.getGlobalComments(
                slugs.workspace,
                slugs.repository,
                scope,
                id,
                page
            )

            comments.addAll(response.values)
            var nextPage = response.nextPage
            while (nextPage != null) {
                val tmp = commentRepository.getGlobalComments(
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

    private fun flattenComments(): MutableList<TreeNode<Comment>> {
        val result = mutableListOf<TreeNode<Comment>>()
        comments.forEach { comment ->
            result.addAll(comment.flatten())
        }

        return result
    }

    private fun updateComment(newComment: Comment) {
        if (newComment.parent != null) {
            comments.forEach { comments ->
                comments.findById(newComment.parent.id)?.replaceChild(newComment.id, newComment)
            }
        } else {
            comments.find { parent ->
                parent.id == newComment.id
            }?.let {
                val newNode = it.copy()
                newNode.element = newComment
                comments.replaceElement(it, newNode)
            }
        }
    }
}
