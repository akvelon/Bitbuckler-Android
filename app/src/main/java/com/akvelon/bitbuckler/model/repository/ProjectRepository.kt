/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 02 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Workspace

class ProjectRepository(
    private val api: BitApi
) {

    suspend fun getProjects(
        workspaceSlug: String,
        page: String?
    ) = api.getProjects(workspaceSlug, page)

    suspend fun getAllProjects(
        workspaceSlug: String,
    ): MutableList<Project> {
        val list = mutableListOf<Project>()
        var repeatFlag = true
        var page = 1
        while (repeatFlag) {
            val loaded = getProjects(workspaceSlug, page.toString()).values
            repeatFlag = loaded.isNotEmpty()
            list.addAll(loaded)
            ++page
        }
        return list
    }
}
