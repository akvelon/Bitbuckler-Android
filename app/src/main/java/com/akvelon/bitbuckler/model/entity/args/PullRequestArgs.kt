/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 February 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PullRequestArgs(

    val workspaceSlug: String,

    val repositorySlug: String,

    val id: Int,

    var title: String? = null
) : Parcelable
