/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 10 March 2022
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class WorkspaceSizeInfo(

    @SerializedName("workspace_slug")
    val workspaceSlug: String,

    val size: Int? = null,

    @SerializedName("last_update")
    val lastUpdate: Date,

    val users: List<String>

) : Parcelable {
    constructor() : this("", null, Date(), listOf())
}
