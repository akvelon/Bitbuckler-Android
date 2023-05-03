/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest.comment.inline

import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import com.akvelon.bitbuckler.ui.state.screen.CommentList
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface InlineCommentsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideBottomNav()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showInlineComments(
        file: FileDiff,
        line: LineDiff?,
        comments: List<TreeNode<Comment>>,
        account: User
    )

    @StateStrategyType(SkipStrategy::class)
    fun showError(error: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDeleteCommentDialog(comment: Comment)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideDeleteCommentDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDeleteCommentProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateComments(comments: List<TreeNode<Comment>>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyError(show: Boolean, error: Throwable?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showData(data: CommentList)

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(error: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun resetCommentField()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCommentField(content: String, resId: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showReplyToField(commentAuthor: String)
}
