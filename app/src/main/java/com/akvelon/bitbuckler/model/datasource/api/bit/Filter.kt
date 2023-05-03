/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 15 January 2021
 */

package com.akvelon.bitbuckler.model.datasource.api.bit

import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueFilter

// TODO This file should be re-worked in scope of BIT-125
object Filter {

    const val GLOBAL_COMMENTS = "inline.path=null"
    const val INLINE_COMMENTS = "inline.path!=null"
    const val UPDATED_ON_DESC = "-updated_on"

    fun myPrs(accountId: String) =
        "state=\"OPEN\" AND author.account_id=\"$accountId\""

    fun prsImReviewing(accountId: String) =
        "state=\"OPEN\" AND reviewers.account_id=\"$accountId\""

    fun repositoriesByProject(projectKey: String) = "project.key=\"$projectKey\""

    fun prsInRepo(state: PullRequestState) = "state=\"$state\""

    fun issues(filter: IssueFilter, accountId: String, queryString: String) = when (filter) {
        IssueFilter.ALL -> ""
        IssueFilter.OPEN -> "state=\"open\" OR state=\"new\""
        IssueFilter.MINE -> "assignee.account_id=\"$accountId\""
    }.run {
        if (queryString.isNotEmpty())
            if (this.isNotEmpty())
                "$this AND title ~ \"$queryString\""
            else
                "title ~ \"$queryString\""
        else
            this
    }
}
