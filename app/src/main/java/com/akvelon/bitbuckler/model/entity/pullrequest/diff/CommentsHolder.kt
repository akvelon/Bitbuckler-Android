/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 09 December 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff

import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment

interface CommentsHolder {
    val comments: MutableList<TreeNode<Comment>>
}
