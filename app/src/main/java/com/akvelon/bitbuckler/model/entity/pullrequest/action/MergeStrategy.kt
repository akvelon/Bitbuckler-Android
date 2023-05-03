/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 February 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.action

import com.google.gson.annotations.SerializedName

enum class MergeStrategy(val title: String) {

    @SerializedName("merge_commit")
    MERGE_COMMIT("Merge commit"),

    @SerializedName("squash")
    SQUASH("Squash"),

    @SerializedName("fast_forward")
    FAST_FORWARD("Fast forward")
}
