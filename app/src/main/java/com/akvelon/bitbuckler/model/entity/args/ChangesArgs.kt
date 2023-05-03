/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ChangesArgs(
    val workspaceSlug: String,
    val repositorySlug: String,
    val id: String,
    val scope: ChangesScope,
    var title: String?
) : Parcelable {

    companion object {
        fun fromPullRequestArgs(pullRequestArgs: PullRequestArgs) = with(pullRequestArgs) {
            ChangesArgs(
                workspaceSlug,
                repositorySlug,
                id.toString(),
                ChangesScope.PULL_REQUEST_CHANGES,
                title
            )
        }

        fun fromCommitDetailsArgs(commitDetailsArgs: CommitDetailsArgs) = with(commitDetailsArgs) {
            ChangesArgs(
                workspaceSlug,
                repositorySlug,
                hash,
                ChangesScope.COMMIT_CHANGES,
                title
            )
        }
    }
}
