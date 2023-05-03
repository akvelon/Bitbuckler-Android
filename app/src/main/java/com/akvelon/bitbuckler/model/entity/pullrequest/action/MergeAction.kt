/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 February 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.action

import com.google.gson.annotations.SerializedName

class MergeAction(
    val message: String,

    @SerializedName("close_source_branch")
    val closeSourceBranch: Boolean,

    @SerializedName("merge_strategy")
    val mergeStrategy: MergeStrategy
)
