/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 02 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.Filter
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState

class ActivityRepository(
    private val api: BitApi,
) {

    suspend fun getPullRequests(
        accountId: String, page: String?, state: PullRequestState = PullRequestState.OPEN,
    ) = api.getPullRequestsForUser(accountId, page, Filter.prsInRepo(state))

    suspend fun getRepositoriesWithIssues(page: String?) = api.getPublicRepositories(
        page = page, role = "member", filter = "has_issues=true"
    )
}
