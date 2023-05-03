/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 28 January 2021
 */

package com.akvelon.bitbuckler.ui.state.screen

import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.model.entity.statuses.Status
import com.akvelon.bitbuckler.ui.state.ScreenValue

class PullRequestDetails(
    val pullRequest: PullRequest,

    val account: User,

    val builds: List<Status>
) : ScreenValue {

    override val isEmpty = false
}
