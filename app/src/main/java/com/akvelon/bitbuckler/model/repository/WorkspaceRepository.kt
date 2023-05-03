/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 02 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.datasource.database.dao.ReposLocalStorageDao
import com.akvelon.bitbuckler.model.datasource.database.dao.WorkspaceDao
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.search.CodeSearchResult

class WorkspaceRepository(
    private val api: BitApi,
    private val workspaceDao: WorkspaceDao,
    private val reposLocalStorageDao: ReposLocalStorageDao
) {

    suspend fun getWorkspaces(page: String?) =
        api.getWorkspaces(page)

    suspend fun getAllWorkspaces(): List<Workspace> {
        val workspaces = mutableListOf<Workspace>()
        var repeatFlag = true
        var page = 1
        while (repeatFlag) {
            val loaded = getWorkspaces(page.toString()).values
            repeatFlag = loaded.isNotEmpty()
            workspaces.addAll(loaded)
            ++page
        }
        return workspaces
    }

    suspend fun getCounterForWorkspace(workspaceSlug: String) = reposLocalStorageDao.getTrackedCounterForWorkspace(workspaceSlug)

    suspend fun search(
        workspaceSlug: String,
        searchQuery: String,
        page: String?
    ): PagedResponse<CodeSearchResult> =
        api.searchForCodeInWorkspace(workspaceSlug, searchQuery, page)
}
