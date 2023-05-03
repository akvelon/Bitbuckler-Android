/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 January 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.file

import com.google.gson.annotations.SerializedName

enum class FileStatus {

    @SerializedName("added")
    ADDED,

    @SerializedName("removed")
    REMOVED,

    @SerializedName("modified")
    MODIFIED,

    @SerializedName("renamed")
    RENAMED,

    @SerializedName("merge conflict")
    MERGE_CONFLICT,

    @SerializedName("remote deleted")
    REMOTE_DELETED
}
