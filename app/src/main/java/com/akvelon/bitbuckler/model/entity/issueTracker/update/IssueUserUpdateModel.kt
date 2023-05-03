package com.akvelon.bitbuckler.model.entity.issueTracker.update

import com.google.gson.annotations.SerializedName

class IssueUserUpdateModel(
    @SerializedName("account_id")
    val accountId: String,
)