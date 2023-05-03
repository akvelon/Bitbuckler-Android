/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.model.entity.repository

import com.akvelon.bitbuckler.model.api.RepositoryDTO
import com.akvelon.bitbuckler.model.entity.Links
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.RepoLocalData
import com.akvelon.bitbuckler.ui.state.ScreenValue
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Suppress("LongParameterList")
data class Repository(

    val uuid: String,

    val name: String,

    @SerializedName("full_name")
    val fullName: String,

    val project: Project,

    val isPrivate: Boolean,

    val slug: String,

    val description: String,

    val mainBranch: Branch?,

    val updatedOn: LocalDateTime,

    val size: Long,

    val language: String,

    val links: Links,

    val hasIssues: Boolean,

    var isTracked: Boolean,

    val workspaceSlug: String
) : ScreenValue {

    override val isEmpty: Boolean
        get() = false

    fun toRecent() =
        RecentRepository(
            this.uuid,
            this.name,
            this.isPrivate,
            this.slug,
            this.fullName.split("/").first(),
            LocalDateTime.now(),
            this.links
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Repository

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

}
