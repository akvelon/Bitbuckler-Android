/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 January 2021
 */

package com.akvelon.bitbuckler.model.entity.comment

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.User
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Comment(
    val id: Int,

    var deleted: Boolean,

    val user: User,

    val content: Content,

    @SerializedName("created_on")
    val createdOn: LocalDateTime,

    @SerializedName("updated_on")
    val updatedOn: LocalDateTime?,

    val parent: Parent?,

    val inline: Inline?
) : Parcelable {

    val isParent
        get() = parent == null

    val isFileComment
        get() = inline?.from == null && inline?.to == null

    fun updateComment(newContent: Content) =
        Comment(
            id,
            deleted,
            user,
            newContent,
            createdOn,
            updatedOn,
            parent,
            inline
        )
}
