/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 июнь 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import kotlinx.parcelize.Parcelize

@Parcelize
class MergeActionArgs(
    val pullRequestId: Int,

    val pullRequestTitle: String,

    val sourceBranch: String,

    val targetBranch: String,

    val closeSourceBranch: Boolean
) : Parcelable {

    companion object {
        fun fromPullRequest(pullRequest: PullRequest) =
            MergeActionArgs(
                pullRequest.id,
                pullRequest.title,
                pullRequest.source.branch.name,
                pullRequest.destination.branch.name,
                pullRequest.closeSourceBranch
            )
    }
}
