/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 January 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.file

import com.google.gson.annotations.SerializedName

class File(

    val path: String,

    @SerializedName("escaped_path")
    val escapedPath: String,

    val type: String
)
