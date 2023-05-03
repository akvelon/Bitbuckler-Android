package com.akvelon.bitbuckler.domain.repo

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akvelon.bitbuckler.model.datasource.local.Prefs
import com.akvelon.bitbuckler.model.entity.CachedRepoData
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.model.entity.pullrequest.toPullRequestState
import com.akvelon.bitbuckler.model.entity.slugs
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.PullRequestRepository
import com.akvelon.bitbuckler.model.repository.local.CachedReposRepository
import com.akvelon.bitbuckler.model.repository.pullrequest.TrackedPullRequestsRepo

class SyncPRsWorker(
    context: Context, parameters: WorkerParameters,
    private val prRepo: PullRequestRepository,
    private val prefs: Prefs,
    private val trackedPullRequestsRepo: TrackedPullRequestsRepo,
    private val accountRepository: AccountRepository,
    private val cachedReposRepository: CachedReposRepository,
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val trackedRepos = cachedReposRepository.getTrackedRepos()
        val trackedPrs = trackedRepos.map { repo ->
            fetchAllPullRequests(repo, prefs.activityFilterState.position.toPullRequestState())
        }.flatten()
        trackedPullRequestsRepo.clearAll()
        trackedPullRequestsRepo.addAll(trackedPrs)
        return Result.success()
    }

    private suspend fun fetchAllPullRequests(
        data: CachedRepoData,
        state: PullRequestState = PullRequestState.OPEN,
    ): ArrayList<TrackedPullRequest> {
        var page = 1
        var repeatFlag = true
        val currentUser = accountRepository.getCurrentAccount()
        val array = arrayListOf<TrackedPullRequest>()

        while (repeatFlag) {
            val items = prRepo.getPullRequests(
                data.workspaceSlug,
                data.slug,
                page.toString(),
                state
            ).values.map {
                prRepo.getPullRequestById(
                    data.workspaceSlug,
                    data.slug,
                    it.id
                )
            }.filter { pr ->
                pr.reviewers?.find { participant -> participant.user.uuid == currentUser.uuid } != null ||
                        pr.participants?.find { participant -> participant.user.uuid == currentUser.uuid } != null ||
                        pr.author.uuid == currentUser.uuid
            }.map {
                it.toTracked(repoUuid = data.uuid, data.slugs(), currentUser)
            }
            array.addAll(items)
            repeatFlag = items.isNotEmpty()
            ++page
        }
        return array
    }
}