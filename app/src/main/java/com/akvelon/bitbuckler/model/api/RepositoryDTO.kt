/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.model.api

import com.akvelon.bitbuckler.model.entity.Links
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Suppress("LongParameterList")
data class RepositoryDTO(

    val uuid: String,

    val name: String,

    @SerializedName("full_name")
    val fullName: String,

    val project: Project,

    val workspace: Workspace? = null,

    @SerializedName("is_private")
    val isPrivate: Boolean,

    val slug: String,

    val description: String,

    @SerializedName("mainbranch")
    val mainBranch: Branch?,

    @SerializedName("updated_on")
    val updatedOn: LocalDateTime,

    val size: Long,

    val language: String,

    val links: Links,

    @SerializedName("has_issues")
    val hasIssues: Boolean
)
