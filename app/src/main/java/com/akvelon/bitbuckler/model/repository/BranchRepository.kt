/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi

class BranchRepository(
    private val api: BitApi
) {

    suspend fun getBranches(
        workspaceSlug: String,
        repositorySlug: String,
        page: String?,
    ) = api.getBranches(workspaceSlug, repositorySlug, page)

    suspend fun getTags(
        workspaceSlug: String,
        repositorySlug: String,
        page: String?
    ) = api.getTags(workspaceSlug, repositorySlug, page)
}
