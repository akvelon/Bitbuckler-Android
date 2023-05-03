/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 08 February 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.comment.Inline
import kotlinx.parcelize.Parcelize

@Parcelize
class NewCommentArgs(

    val commentScope: CommentsArgs,

    var parentComment: Comment? = null,

    var inline: Inline? = null,

    var editableComment: Comment? = null
) : Parcelable
