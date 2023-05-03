/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 December 2020
 */

package com.akvelon.bitbuckler.model.entity.statuses

import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class Status(

    val name: String,

    val key: String,

    val description: String,

    val repository: Repository,

    val url: String,

    val state: StatusState,

    val commit: Commit,

    @SerializedName("created_on")
    val createdOn: LocalDateTime,

    @SerializedName("updated_on")
    val updatedOn: LocalDateTime
)
