/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 28 January 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Entity
class TrackedPullRequest(

    @PrimaryKey(autoGenerate = true)
    val idInDB: Int? = null,

    val repoUuid: String? = null,

    val workspaceSlug: String,

    val repositorySlug: String,

    @SerializedName("id")
    val idInRepository: Int,

    val title: String,

    var state: PullRequestState,

    val authorDisplayName: String,

    val authorAvatar: String,

    val commentCount: Int,

    val createdOn: LocalDateTime,

    val updatedOn: LocalDateTime,

    var type: PullRequestType
)
