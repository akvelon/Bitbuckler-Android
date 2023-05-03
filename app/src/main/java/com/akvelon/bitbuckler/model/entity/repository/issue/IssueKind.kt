/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 05 February 2021
 */

package com.akvelon.bitbuckler.model.entity.repository.issue

import com.google.gson.annotations.SerializedName

enum class IssueKind {
    @SerializedName("bug")
    BUG,

    @SerializedName("enhancement")
    ENHANCEMENT,

    @SerializedName("proposal")
    PROPOSAL,

    @SerializedName("task")
    TASK
}
