/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.comment.list

import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.ui.state.screen.CommentList
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface CommentListView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyError(show: Boolean, error: Throwable?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showData(data: CommentList)

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(error: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateComments(comments: List<TreeNode<Comment>>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCommentDeleteDialog(comment: Comment)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideCommentDeleteDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFullScreenProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun resetCommentField()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCommentField(content: String, resId: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showReplyToField(commentAuthor: String)
}
