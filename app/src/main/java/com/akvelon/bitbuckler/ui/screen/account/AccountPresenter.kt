/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.account

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatDelegate.NightMode
import com.akvelon.bitbuckler.domain.repo.CachedReposUseCase
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.AuthRepository
import com.akvelon.bitbuckler.model.repository.RepoRepository
import com.akvelon.bitbuckler.model.repository.SettingsRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.model.repository.analytics.models.AnalyticsParameter.Theme
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.ACCOUNT
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.billing.BillingUseCase
import com.akvelon.bitbuckler.ui.screen.Screen
import com.akvelon.bitbuckler.ui.screen.privacypolicy.LOAD_TYPE_ACCOUNT
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moxy.InjectViewState
import moxy.presenterScope

@InjectViewState
class AccountPresenter(
    router: Router,
    private val analytics: AnalyticsProvider,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val repoRepository: RepoRepository,
    private val cachedReposUseCase: CachedReposUseCase,
    private val settingsRepository: SettingsRepository,
    private val billingUseCase: BillingUseCase
) : BasePresenter<AccountView>(router) {

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Profile.ProfileScreen)

        billingUseCase.isSubscriptionActive
            .onEach(viewState::showSubscriptionStatus)
            .launchIn(presenterScope)

        getAccount()

        val donationIsMade = accountRepository.getDonationStatus()
        if (donationIsMade)
            viewState.showDonationStatus()
    }

    fun getProductDetails() {
        billingUseCase.getProductDetails()
    }

    fun saveDonation() {
        accountRepository.setDonationStatus(true)
        viewState.showDonationStatus()
    }

    fun getAccount(clearCache: Boolean = false) = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            NonFatalError.log(throwable, screenName)

            launchOnMain {
                viewState.hideProgress()
                viewState.showError(throwable)
            }
        }
    ) {
        val user = accountRepository.getCurrentAccount(clearCache)
        switchToUi {
            if (clearCache) { viewState.hideProgress() }
            viewState.showAccount(user)
        }
    }

    fun loadPrivacyPolicyContent() = launchOnMain {
        router.navigateTo(Screen.privacyPolicy(LOAD_TYPE_ACCOUNT), false)
    }

    fun saveNightMode(@NightMode nightMode: Int) {
        settingsRepository.setNightMode(nightMode)
    }

    fun onLogout() = viewState.showConfirm()

    fun onDismissMessage() = viewState.hideConfirm()

    fun logout() = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain { viewState.showError(throwable) }
        }
    ) {
        authRepository.logout()
        cachedReposUseCase.clearTracked()
        cachedReposUseCase.clear()
        repoRepository.clearAllRecent()
        analytics.report(AnalyticsEvent.Profile.Logout)
    }

    fun onLinkClick() {
        analytics.report(AnalyticsEvent.Profile.ProfileAkvelonInc)
    }

    fun onActivityNotFound(error: ActivityNotFoundException) {
        NonFatalError.log(error, screenName)
    }

    private fun getModeTitle(nightMode: Int) = when (nightMode) {
        1 -> Theme.LIGHT
        2 -> Theme.DARK
        else -> Theme.DEFAULT
    }

    fun onRateAppClicked() {
        analytics.report(AnalyticsEvent.Profile.ProfileRate)
    }

    fun onDonationDialogOpened() {
        analytics.report(AnalyticsEvent.DonationEvents.DonationScreen)
    }

    fun onDonationDialogClosed() {
        analytics.report(AnalyticsEvent.DonationEvents.CloseDonationScreen)
    }

    companion object {
        private const val screenName = ACCOUNT
    }
}
