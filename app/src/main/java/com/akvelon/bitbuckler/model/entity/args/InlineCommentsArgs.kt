/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 April 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import kotlinx.parcelize.Parcelize
import java.util.Collections.addAll

@Parcelize
class InlineCommentsArgs(

    val commentsArgs: CommentsArgs,

    val file: FileDiff,

    val line: LineDiff?
) : Parcelable {
    val comments: MutableList<TreeNode<Comment>>
        get() = mutableListOf<TreeNode<Comment>>().apply {
            addAll(file.comments)
            line?.let { addAll(it.comments) }
        }
}
