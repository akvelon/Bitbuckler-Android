package com.akvelon.bitbuckler.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.repository.Repository
import java.time.LocalDateTime

@Entity
data class CachedRepoData(
    @PrimaryKey
    val uuid: String,
    val workspaceSlug: String,
    val updatedDate: LocalDateTime,
    val name: String,
    val fullName: String,
    val project: Project,
    val projectKey: String = project.key,
    val isPrivate: Boolean,
    val slug: String,
    val description: String,
    val mainBranch: Branch?,
    val updatedOn: LocalDateTime,
    val size: Long,
    val language: String,
    val links: Links,
    val hasIssues: Boolean,
    var isTracked: Boolean
)

fun CachedRepoData.slugs() = Slugs(
    workspace = fullName.split("/").first(),
    repository = fullName.split("/").last()
)

fun Repository.toCached(
    workspaceSlug: String,
    updatedDate: LocalDateTime) = with(this) {
    CachedRepoData(
        workspaceSlug = workspaceSlug,
        updatedDate = updatedDate,
        isTracked = isTracked,
        uuid = uuid,
        name = name,
        fullName = fullName,
        project = project,
        isPrivate = isPrivate,
        slug = slug,
        description = description,
        mainBranch = mainBranch,
        updatedOn = updatedOn,
        size = size,
        language = language,
        links = links,
        hasIssues = hasIssues
    )
}

fun CachedRepoData.toRepository() = with(this) {
    Repository(
        uuid = uuid,
        name = name,
        fullName = fullName,
        project = project,
        isPrivate = isPrivate,
        slug = slug,
        description = description,
        mainBranch = mainBranch,
        updatedOn = updatedOn,
        size = size,
        language = language,
        links = links,
        hasIssues = hasIssues,
        isTracked = isTracked,
        workspaceSlug = workspaceSlug
    )
}