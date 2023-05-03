package com.akvelon.bitbuckler.model.mappers

import com.akvelon.bitbuckler.model.api.RepositoryDTO
import com.akvelon.bitbuckler.model.entity.RepoLocalData
import com.akvelon.bitbuckler.model.entity.repository.Repository

object Mapper {
    fun repositoryMapper(
        dto: RepositoryDTO,
        isTrackedFlag: Boolean,
        workspaceSlug: String,
    ): Repository {
        return Repository(
            uuid = dto.uuid,
            name = dto.name,
            fullName = dto.fullName,
            project = dto.project,
            isPrivate = dto.isPrivate,
            slug = dto.slug,
            description = dto.description,
            mainBranch = dto.mainBranch,
            updatedOn = dto.updatedOn,
            size = dto.size,
            language = dto.language,
            links = dto.links,
            hasIssues = dto.hasIssues,
            isTracked = isTrackedFlag,
            workspaceSlug = workspaceSlug,
        )
    }
}