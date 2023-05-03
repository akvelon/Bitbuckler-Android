/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 February 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.action

object MergeStrategyCollection {

    val items = listOf(
        MergeStrategyItem(MergeStrategy.MERGE_COMMIT, "git merge --no-ff"),
        MergeStrategyItem(MergeStrategy.SQUASH, "git merge --squash"),
        MergeStrategyItem(MergeStrategy.FAST_FORWARD, "git merge --ff-only")
    )
}
