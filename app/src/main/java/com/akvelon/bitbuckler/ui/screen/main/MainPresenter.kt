/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 08 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.main

import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.SettingsRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class MainPresenter(
    private val settingsRepository: SettingsRepository,
    private val accountRepository: AccountRepository,
    private val analytics: AnalyticsProvider,
) : MvpPresenter<MainView>() {

    override fun onFirstViewAttach() {
        if (settingsRepository.shouldShowDonationDialog())
            viewState.showDonationDialog()
        else if (settingsRepository.getAppRunCount() > MIN_APP_RUN_COUNT)
            viewState.showRateDialog()
    }

    fun showBottomNav() {
        viewState.showBottomNav()
    }

    fun hideBottomNav() {
        viewState.hideBottomNav()
    }

    fun saveDonation() {
        accountRepository.setDonationStatus(true)
    }

    fun setDonationReminderStatus(status: Boolean) {
        if (!status)
            analytics.report(AnalyticsEvent.DonationEvents.CloseDonationScreenNotRemind)
        accountRepository.setDonationReminderStatus(status)
    }

    fun onDonationDialogOpened() {
        analytics.report(AnalyticsEvent.DonationEvents.DonationScreen)
    }

    fun onDonationDialogClosed() {
        analytics.report(AnalyticsEvent.DonationEvents.CloseDonationScreen)
    }

    companion object {
        const val MIN_APP_RUN_COUNT = 4
        const val DONATION_SHOWING_OCCURRENCE = 10
    }
}
