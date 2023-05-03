/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.extension.dropFirst
import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.entity.args.ChangesArgs
import com.akvelon.bitbuckler.model.entity.args.ChangesScope
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import okhttp3.ResponseBody

class ChangesRepository(
    private val api: BitApi
) {
    suspend fun getChanges(changesArgs: ChangesArgs) =
        with(changesArgs) {
            when (scope) {
                ChangesScope.PULL_REQUEST_CHANGES ->
                    getPullRequestDiff(
                        workspaceSlug,
                        repositorySlug,
                        id.toInt()
                    )
                ChangesScope.COMMIT_CHANGES ->
                    getCommitDiff(
                        workspaceSlug,
                        repositorySlug,
                        id
                    )
            }
        }

    suspend fun getFiles(changesArgs: ChangesArgs) =
        with(changesArgs) {
            when (scope) {
                ChangesScope.PULL_REQUEST_CHANGES ->
                    getPullRequestDiffstat(
                        workspaceSlug,
                        repositorySlug,
                        id.toInt()
                    )
                ChangesScope.COMMIT_CHANGES ->
                    getCommitDiffstat(
                        workspaceSlug,
                        repositorySlug,
                        id
                    )
            }
        }

    private suspend fun getCommitDiff(
        workspaceSlug: String,
        repositorySlug: String,
        commitHash: String,
    ): List<FileDiff> = parseDiffResponseBody(api.getCommitDiff(workspaceSlug, repositorySlug, commitHash))

    private suspend fun getCommitDiffstat(
        workspaceSlug: String,
        repositorySlug: String,
        commitHash: String,
    ) = api.getCommitDiffStat(workspaceSlug, repositorySlug, commitHash)

    private suspend fun getPullRequestDiff(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ): List<FileDiff> =
        parseDiffResponseBody(api.getPullRequestDiff(workspaceSlug, repositorySlug, pullRequestId))

    private suspend fun getPullRequestDiffstat(
        workspaceSlug: String,
        repositorySlug: String,
        pullRequestId: Int
    ) = api.getPullRequestDiffStat(workspaceSlug, repositorySlug, pullRequestId)

    private fun parseDiffResponseBody(responseBody: ResponseBody) = with(responseBody) {
        string()
            .split(Regex("(^|\n)diff --git "))
            .dropFirst()
            .map { item -> FileDiff.parse(item) }
    }
}
