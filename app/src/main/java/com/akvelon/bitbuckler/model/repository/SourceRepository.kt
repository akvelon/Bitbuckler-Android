/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 29 April 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi

class SourceRepository(
    private val api: BitApi
) {

    suspend fun getFiles(
        workspaceSlug: String,
        repositorySlug: String,
        commitHash: String,
        path: String,
        page: String?
    ) = api.getFiles(workspaceSlug, repositorySlug, commitHash, path, page)

    suspend fun getFileContent(
        workspaceSlug: String,
        repositorySlug: String,
        commitHash: String,
        filePath: String
    ) = api.getFileContent(workspaceSlug, repositorySlug, commitHash, filePath)
}
