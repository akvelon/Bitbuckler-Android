/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 May 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.Slugs
import kotlinx.parcelize.Parcelize

@Parcelize
class CommentsArgs(
    val slugs: Slugs,

    val scope: CommentScope,

    val id: String
) : Parcelable {

    companion object {

        fun fromChangesArgs(changesArgs: ChangesArgs) = with(changesArgs) {
            val commentScope = when (scope) {
                ChangesScope.COMMIT_CHANGES -> CommentScope.COMMITS
                ChangesScope.PULL_REQUEST_CHANGES -> CommentScope.PULL_REQUESTS
            }
            CommentsArgs(
                Slugs(
                    workspaceSlug,
                    repositorySlug
                ),
                commentScope,
                id
            )
        }

        fun fromPullRequestArgs(pullRequestArgs: PullRequestArgs) = with(pullRequestArgs) {
            CommentsArgs(
                Slugs(
                    workspaceSlug,
                    repositorySlug
                ),
                CommentScope.PULL_REQUESTS,
                id.toString()
            )
        }

        fun fromCommitDetailsArgs(commitDetailsArgs: CommitDetailsArgs) = with(commitDetailsArgs) {
            CommentsArgs(
                Slugs(
                    workspaceSlug,
                    repositorySlug
                ),
                CommentScope.COMMITS,
                hash
            )
        }

        fun fromIssuesArgs(issueArgs: IssueArgs) = with(issueArgs) {
            CommentsArgs(
                Slugs(
                    workspaceSlug,
                    repositorySlug
                ),
                CommentScope.ISSUES,
                id.toString()
            )
        }
    }
}
