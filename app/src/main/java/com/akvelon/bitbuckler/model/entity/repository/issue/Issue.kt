/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 05 February 2021
 */

package com.akvelon.bitbuckler.model.entity.repository.issue

import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.User
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class Issue(
    val priority: IssuePriority,

    val kind: IssueKind,

    val reporter: User,

    val title: String,

    val votes: Int,

    val watches: Int,

    val assignee: User?,

    val state: IssueState,

    val content: Content,

    @SerializedName("created_on")
    val createdOn: LocalDateTime,

    @SerializedName("updated_on")
    val updatedOn: LocalDateTime,

    val id: Int
)
