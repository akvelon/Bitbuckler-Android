/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 May 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.Filter
import com.akvelon.bitbuckler.model.entity.args.CommentScope
import com.akvelon.bitbuckler.model.entity.comment.NewComment

class CommentRepository(
    private val api: BitApi
) {

    suspend fun getGlobalComments(
        workspaceSlug: String,
        repositorySlug: String,
        commentScope: CommentScope,
        id: String,
        page: String? = null
    ) = api.getComments(
        workspaceSlug,
        repositorySlug,
        commentScope.slug,
        id,
        if (commentScope == CommentScope.ISSUES) "" else Filter.GLOBAL_COMMENTS,
        page
    )

    suspend fun getInlineComments(
        workspaceSlug: String,
        repositorySlug: String,
        commentScope: CommentScope,
        id: String,
        page: String? = null
    ) = api.getComments(
        workspaceSlug,
        repositorySlug,
        commentScope.slug,
        id,
        Filter.INLINE_COMMENTS,
        page
    )

    suspend fun addNewComment(
        workspaceSlug: String,
        repositorySlug: String,
        commentScope: CommentScope,
        id: String,
        comment: NewComment
    ) = api.addNewComment(
        workspaceSlug,
        repositorySlug,
        commentScope.slug,
        id,
        comment
    )

    @Suppress("LongParameterList")
    suspend fun editComment(
        workspaceSlug: String,
        repositorySlug: String,
        commentScope: CommentScope,
        scopeId: String,
        commentId: Int,
        comment: NewComment
    ) = api.editComment(
        workspaceSlug,
        repositorySlug,
        commentScope.slug,
        scopeId,
        commentId,
        comment
    )

    suspend fun deleteComment(
        workspaceSlug: String,
        repositorySlug: String,
        commentScope: CommentScope,
        id: String,
        commentId: Int
    ) = api.deleteComment(
        workspaceSlug,
        repositorySlug,
        commentScope.slug,
        id,
        commentId
    )
}
