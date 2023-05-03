/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 May 2021
 */

package com.akvelon.bitbuckler.ui.state.screen

import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.ui.state.ScreenValue

class CommentList(
    val comments: List<TreeNode<Comment>>,

    val account: User
) : ScreenValue {

    override val isEmpty = false
}
