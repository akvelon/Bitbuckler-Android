/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.activity

import android.content.SharedPreferences
import com.akvelon.bitbuckler.App
import com.akvelon.bitbuckler.domain.repo.GetAllTrackedIssuesUseCases
import com.akvelon.bitbuckler.domain.repo.GetAllTrackedPRsUseCase
import com.akvelon.bitbuckler.domain.repo.GetAllUserIssuesWithCommentsUseCase
import com.akvelon.bitbuckler.domain.repo.GetAllUserPRsUseCase
import com.akvelon.bitbuckler.model.datasource.local.LocalFlags
import com.akvelon.bitbuckler.model.datasource.local.Prefs
import com.akvelon.bitbuckler.model.entity.DataState
import com.akvelon.bitbuckler.model.entity.args.IssueArgs
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.model.repository.SettingsRepository
import com.akvelon.bitbuckler.model.repository.WorkspaceSizeRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.ACTIVITY_LIST
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.billing.BillingUseCase
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.InjectViewState
import moxy.presenterScope

@InjectViewState
class ActivityListPresenter(
    router: Router,
    prefs: Prefs,
    private val preferences: SharedPreferences,
    private val analytics: AnalyticsProvider,
    private val workspaceSizeRepository: WorkspaceSizeRepository,
    private val settingsRepository: SettingsRepository,
    private val getAllUserPRs: GetAllUserPRsUseCase,
    private val getAllUserIssuesWithComments: GetAllUserIssuesWithCommentsUseCase,
    private val getAllTrackedPRsUseCase: GetAllTrackedPRsUseCase,
    private val getAllTrackedIssuesUseCases: GetAllTrackedIssuesUseCases,
    private val billingUseCase: BillingUseCase,
) : BasePresenter<ActivityListView>(router) {

    private var filterState = PullRequestState.OPEN

    init {
        prefs.activityFilterState = filterState
    }

    fun isPremiumActive() = billingUseCase.isSubscriptionActive.value

    val trackedIssues = MutableStateFlow<DataState<List<TrackedIssue>>>(DataState.Loading)
    val trackedPrs = MutableStateFlow<DataState<List<TrackedPullRequest>>>(DataState.Loading)
    val userPrs = MutableStateFlow<DataState<List<TrackedPullRequest>>>(DataState.Loading)
    val userIssues = MutableStateFlow<DataState<List<TrackedIssue>>>(DataState.Loading)
    val allNormalUserPrs = MutableStateFlow<DataState<List<TrackedPullRequest>>>(DataState.Loading)

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Activity.ActivityScreen)

        billingUseCase.isSubscriptionActive.onEach(viewState::reflectPremiumStatus)
            .onEach(this::reflectPremiumStatus)
            .launchIn(presenterScope)
    }

    private fun reflectPremiumStatus(isPremium: Boolean) {
        if (isPremium) refreshPremiumUserActivity(false)
        else refreshNormalUserActivity(false)
    }

    fun refreshActivity(withLoader: Boolean) {
        if (billingUseCase.isSubscriptionActive.value) refreshPremiumUserActivity(withLoader)
        else refreshNormalUserActivity(withLoader)
    }

    fun startPremiumFlow() {
        billingUseCase.getProductDetails()
    }

    private fun refreshPremiumUserActivity(withLoader: Boolean) {
        presenterScope.launch(Dispatchers.IO) {
            if (withLoader) {
                trackedPrs.emit(DataState.Loading)
                trackedIssues.emit(DataState.Loading)
            }
            val allUserPrs = withContext(Dispatchers.IO) {
                val data = getAllUserPRs.getUserAllPullRequests(filterState)
                userPrs.emit(data)
                data
            }
            val allUserIssues = withContext(Dispatchers.IO) {
                val data = getAllUserIssuesWithComments.getUserAllIssues()
                userIssues.emit(data)
                data
            }
            val allTrackedPrs = withContext(Dispatchers.IO) {
                getAllTrackedPRsUseCase.getAll()
            }
            val allTrackedIssues = withContext(Dispatchers.IO) {
                getAllTrackedIssuesUseCases.getAll()
            }
            trackedIssues.emit(getJoinedIssuesDataState(allUserIssues, allTrackedIssues))
            trackedPrs.emit(getJoinedPrsDataState(allUserPrs, allTrackedPrs))

            switchToUi {
                viewState.showRefreshProgress(false)
            }
        }
    }

    private fun getJoinedIssuesDataState(
        allUserIssues: DataState<List<TrackedIssue>>,
        allTrackedIssues: DataState<List<TrackedIssue>>,
    ): DataState<List<TrackedIssue>> {
        return if (allUserIssues is DataState.Success && allTrackedIssues is DataState.Success && allTrackedIssues.data.isNotEmpty()) {
            val allPrs = (allTrackedIssues.data + allUserIssues.data).distinctBy { it.id }
            DataState.Success(allPrs)
        } else {
            allTrackedIssues
        }
    }

    private fun refreshNormalUserActivity(withLoader: Boolean) {
        presenterScope.launch(Dispatchers.IO) {
            val trackedPrs = withContext(Dispatchers.IO) {
                getAllTrackedPRsUseCase.getAll()
            }
            val userPrsResponse = withContext(Dispatchers.IO) {
                getAllUserPRs.getUserAllPullRequests(filterState)
            }
            val dataState = getJoinedPrsDataState(userPrsResponse, trackedPrs)
            allNormalUserPrs.emit(dataState)
            switchToUi {
                viewState.showRefreshProgress(false)
            }
        }
    }

    private fun getJoinedPrsDataState(
        userPrsResponse: DataState<List<TrackedPullRequest>>,
        trackedPrs: DataState<List<TrackedPullRequest>>,
    ): DataState<List<TrackedPullRequest>> {
        return when {
            userPrsResponse is DataState.Success && trackedPrs is DataState.Success -> {
                val allPrs = (trackedPrs.data + userPrsResponse.data).distinctBy { it.idInRepository }
                DataState.Success(allPrs)
            }
            userPrsResponse is DataState.Success -> {
                userPrsResponse
            }
            trackedPrs is DataState.Success -> {
                trackedPrs
            }
            else -> userPrsResponse
        }
    }

    fun onRefreshRequestedByUser() {
        analytics.report(AnalyticsEvent.Activity.ActivityPullToRefresh)
    }

    fun onPullRequestClick(trackedPullRequest: TrackedPullRequest) {
        router.navigateTo(Screen.pullRequestDetails(PullRequestArgs(trackedPullRequest.workspaceSlug,
            trackedPullRequest.repositorySlug,
            trackedPullRequest.idInRepository)))

        launchOnDefault {
            workspaceSizeRepository.handleWorkspaceSizeEvent(trackedPullRequest.workspaceSlug, screenName)
        }
    }

    fun onIssueClick(issue: TrackedIssue) {
        router.navigateTo(Screen.issueDetails(IssueArgs(id = issue.id,
            workspaceSlug = issue.workspaceSlug,
            repositorySlug = issue.repositorySlug)))
    }

    fun navigateToTrackRepoScreen() {
        router.navigateTo(Screen.trackRepoSelection())
        analytics.report(AnalyticsEvent.Activity.ActivityTrackReposOpen)
    }

    fun isShowTrackRepoDialog() = launchOnDefault {
        val shouldShowDonation = settingsRepository.shouldShowDonationDialog()
        val flag = preferences.getBoolean(LocalFlags.TRACK_REPOS_FLAG, false) || App.isTrackReposShown
        //to avoid showing donation and tracking dialogs at the same time, we give priority to donation
        if (!shouldShowDonation) switchToUi {
            viewState.showTrackReposPage(!flag)
            App.isTrackReposShown = true
        }
    }

    companion object {
        private const val screenName = ACTIVITY_LIST
    }
}
