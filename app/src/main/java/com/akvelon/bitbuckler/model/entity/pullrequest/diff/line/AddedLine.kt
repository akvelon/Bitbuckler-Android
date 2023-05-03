/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 February 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.line

import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment
import kotlinx.parcelize.Parcelize

@Parcelize
class AddedLine(
    val newNumber: Int,
    override val content: String,
    override val isConflicted: Boolean,
    override val comments: MutableList<TreeNode<Comment>> = mutableListOf()
) : LineDiff(content, isConflicted, comments) {

    override fun getMaxLineNumber() = newNumber

    override fun oldNumberToString(length: Int) = " ".repeat(length)

    override fun newNumberToString(length: Int) = newNumber.toString().padStart(length)
}
