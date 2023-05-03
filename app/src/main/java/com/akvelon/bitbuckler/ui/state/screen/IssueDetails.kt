/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 21 March 2021
 */

package com.akvelon.bitbuckler.ui.state.screen

import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.ui.state.ScreenValue

class IssueDetails(
    val issue: Issue
) : ScreenValue {

    override val isEmpty = false
}
