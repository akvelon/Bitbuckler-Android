package com.akvelon.bitbuckler.ui.screen.track_repo

import com.akvelon.bitbuckler.model.entity.repository.Repository

data class WorkspaceExtended(
    val name: String,
    val slug: String,
    val trackedNumberPreview: Int
) {
    var repos: Map<String, Repository>? = null

    val listOfRepos
    get() = repos?.values

    val trackedNumber
    get() = repos?.values?.count { it.isTracked } ?: trackedNumberPreview
}

class WorkspaceExpandableTitle(
    var workspace: WorkspaceExtended,
    var expandedTilesNumber: Int = 0
): ExpandableTitle {
    override var isExpanded: Boolean = false
}

interface ExpandableTitle {
    var isExpanded: Boolean
}