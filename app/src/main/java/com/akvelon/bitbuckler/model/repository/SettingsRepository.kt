/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 18 February 2021
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.local.Prefs
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueFilter
import com.akvelon.bitbuckler.ui.screen.main.MainPresenter.Companion.DONATION_SHOWING_OCCURRENCE

class SettingsRepository(
    private val prefs: Prefs
) {
    fun getLastPullRequestState() =
        prefs.lastPullRequestState ?: PullRequestState.OPEN

    fun setLastPullRequestState(state: PullRequestState) {
        prefs.lastPullRequestState = state
    }

    fun getLastIssueFilter() =
        prefs.lastIssueFilter ?: IssueFilter.ALL

    fun setLastIssueFilter(filter: IssueFilter) {
        prefs.lastIssueFilter = filter
    }

    fun increaseAppRunCount() {
        prefs.appRunCount = ++prefs.appRunCount
    }

    fun getAppRunCount() = prefs.appRunCount

    fun shouldShowDonationDialog(): Boolean {
        val reminderStatus = prefs.isDonationReminderSet
        val runCount = getAppRunCount()
        return reminderStatus && runCount > 1 && runCount % DONATION_SHOWING_OCCURRENCE == 0
    }

    fun getNightMode() = prefs.nightMode

    fun setNightMode(nightMode: Int) {
        prefs.nightMode = nightMode
    }
}
