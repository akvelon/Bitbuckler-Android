package com.akvelon.bitbuckler.domain.repo

import com.akvelon.bitbuckler.extension.noNetworkConnection
import com.akvelon.bitbuckler.extension.toTracked
import com.akvelon.bitbuckler.model.entity.DataState
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestType
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.ActivityRepository

class GetAllUserPRsUseCase(
    private val activityRepository: ActivityRepository,
    private val accountRepository: AccountRepository,
) {
    suspend fun getUserAllPullRequests(filterState: PullRequestState): DataState<List<TrackedPullRequest>> {
        return try {
            val user = accountRepository.getCurrentAccount()

            var page = 1
            var repeatFlag = true
            val pullRequests = arrayListOf<TrackedPullRequest>()
            while (repeatFlag) {
                val pageData = activityRepository.getPullRequests(user.accountId, page.toString(), filterState)
                pageData.values.forEach { it.type = PullRequestType.MINE }
                pullRequests.addAll(pageData.values.toTracked())
                repeatFlag = pageData.next != null
                ++page
            }
            DataState.Success(pullRequests)
        } catch (e: Exception) {
            DataState.Error(e.noNetworkConnection)
        }
    }
}