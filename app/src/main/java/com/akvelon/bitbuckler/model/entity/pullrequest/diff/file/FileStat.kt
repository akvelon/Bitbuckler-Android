/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 January 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.file

import com.google.gson.annotations.SerializedName

class FileStat(

    val status: FileStatus,

    val old: File?,

    val new: File?,

    @SerializedName("lines_removed")
    val linesRemoved: Int,

    @SerializedName("lines_added")
    val linesAdded: Int,

    val type: String
) : FilePathHolder {

    override val filePath
        get() = if (isRemoved()) old?.path.toString() else new?.path.toString()

    val isMergeConflict
        get() = status == FileStatus.MERGE_CONFLICT || status == FileStatus.REMOTE_DELETED

    private fun isRemoved() = status == FileStatus.REMOVED || status == FileStatus.REMOTE_DELETED
}
