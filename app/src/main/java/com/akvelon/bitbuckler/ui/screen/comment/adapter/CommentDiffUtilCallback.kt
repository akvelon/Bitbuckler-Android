/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 05 March 2021
 */

package com.akvelon.bitbuckler.ui.screen.comment.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment

class CommentDiffUtilCallback(
    private val oldComments: List<TreeNode<Comment>>,
    private val newComments: List<TreeNode<Comment>>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldComments.size

    override fun getNewListSize() = newComments.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldComments[oldItemPosition].id == newComments[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldComments[oldItemPosition] == newComments[newItemPosition]
}
