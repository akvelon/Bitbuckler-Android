/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.state.screen

import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.model.entity.statuses.Status
import com.akvelon.bitbuckler.ui.state.ScreenValue

class CommitDetails(
    val commit: Commit,

    val account: User,

    val build: List<Status>
) : ScreenValue {
    override val isEmpty = false
}
