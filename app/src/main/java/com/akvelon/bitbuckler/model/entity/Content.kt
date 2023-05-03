/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 December 2020
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Content(
    val raw: String,

    val markup: String,

    val html: String,

    val type: String,

    val mentionedRaw: String? = null
) : Parcelable {

    fun setMentionedRaw(newMentionedRaw: String) =
        Content(
            raw,
            markup,
            html,
            type,
            newMentionedRaw
        )

    companion object {
        const val ZERO_WIDTH = 8204.toChar()
        const val EMPTY_RAW = "\r\n"
    }
}
