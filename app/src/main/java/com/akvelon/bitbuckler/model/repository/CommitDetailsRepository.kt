/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi

class CommitDetailsRepository(
    private val api: BitApi
) {
    suspend fun getCommitDetails(
        workspaceSlug: String,
        repositorySlug: String,
        commitHash: String,
    ) = api.getCommitDetails(workspaceSlug, repositorySlug, commitHash)

    suspend fun getCommitStatuses(
        workspaceSlug: String,
        repositorySlug: String,
        commitHash: String,
    ) = api.getCommitStatuses(workspaceSlug, repositorySlug, commitHash)
}
