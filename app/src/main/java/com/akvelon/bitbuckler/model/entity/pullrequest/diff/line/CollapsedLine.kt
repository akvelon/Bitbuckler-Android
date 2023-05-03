/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 February 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.line

import com.akvelon.bitbuckler.extension.dropFirstSymbol
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment
import kotlinx.parcelize.Parcelize

@Parcelize
class CollapsedLine(
    override val content: String,
    override val comments: MutableList<TreeNode<Comment>> = mutableListOf()
) : LineDiff(content, comments = comments) {

    override fun getMaxLineNumber() = 0

    override fun oldNumberToString(length: Int) = " ".repeat(length)

    override fun newNumberToString(length: Int) = " ".repeat(length)

    companion object {
        fun getLineNumbers(collapsedLine: String): Pair<Int, Int> {
            val numbers = collapsedLine
                .removePrefix(COLLAPSED_LINE_PREFIX)
                .substringBefore(" @@")
                .split(" ")

            val oldNumber = numbers.first().split(",").first().toInt()
            val newNumber = numbers.last().split(",").first().dropFirstSymbol().toInt()

            return oldNumber to newNumber
        }
    }
}
