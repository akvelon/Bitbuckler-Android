/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 February 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.line

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.CommentsHolder

abstract class LineDiff(
    open val content: String,
    open val isConflicted: Boolean = false,
    override val comments: MutableList<TreeNode<Comment>> = mutableListOf()
) : CommentsHolder, Parcelable {

    abstract fun getMaxLineNumber(): Int

    abstract fun oldNumberToString(length: Int): String

    abstract fun newNumberToString(length: Int): String

    companion object {
        const val EMPTY_CONTENT = ""

        const val ADDED_LINE_PREFIX = "+"
        const val REMOVED_LINE_PREFIX = "-"
        const val COLLAPSED_LINE_PREFIX = "@@ -"

        const val START_CONFLICTED_SECTION = "+<<<<<<< destination:"
        const val DELIMITER_CONFLICTED_SECTION = "+======="
        const val END_CONFLICTED_SECTION = "+>>>>>>> source:"

        val CONFLICTED_LINE_PREFIXES = listOf(
            START_CONFLICTED_SECTION,
            DELIMITER_CONFLICTED_SECTION,
            END_CONFLICTED_SECTION
        )
    }
}
