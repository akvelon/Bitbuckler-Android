/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CommitDetailsArgs(

    val workspaceSlug: String,

    val repositorySlug: String,

    val hash: String,

    var branch: String? = null,

    var title: String? = null
) : Parcelable
