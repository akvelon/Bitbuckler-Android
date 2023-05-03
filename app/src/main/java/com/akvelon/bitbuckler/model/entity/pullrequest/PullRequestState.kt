/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 December 2020
 */

package com.akvelon.bitbuckler.model.entity.pullrequest

enum class PullRequestState(val position: Int) {
    OPEN(0),
    MERGED(1),
    DECLINED(2),
    SUPERSEDED(3)
}

fun Int.toPullRequestState() = when(this) {
    0 -> PullRequestState.OPEN
    1 -> PullRequestState.MERGED
    2 -> PullRequestState.DECLINED
    else -> PullRequestState.SUPERSEDED
}
