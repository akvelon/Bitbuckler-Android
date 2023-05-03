/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 02 December 2020
 */

package com.akvelon.bitbuckler.ui.screen

import android.app.Activity
import com.akvelon.bitbuckler.common.AppFlags
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.toLocalAccount
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.AuthRepository
import com.akvelon.bitbuckler.model.repository.PrivacyPolicyRepo
import com.akvelon.bitbuckler.model.repository.SettingsRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.model.repository.analytics.models.AnalyticsEventNames.LOGIN_FAILED
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.billing.BillingUseCase
import com.android.billingclient.api.ProductDetails
import com.github.terrakok.cicerone.Router
import com.google.firebase.analytics.FirebaseAnalytics.Event.LOGIN
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moxy.InjectViewState
import moxy.presenterScope

@InjectViewState
class AppPresenter(
    router: Router,
    private val privacyPolicyRepo: PrivacyPolicyRepo,
    private val analytics: AnalyticsProvider,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val billingUseCase: BillingUseCase,
) : BasePresenter<AppView>(router) {

    init {

        billingUseCase.getStatusSubject()
            .onEach(viewState::onSubscriptionStatus)
            .launchIn(presenterScope)

        billingUseCase.getProductSubject()
            .onEach(viewState::showSubscriptionsDialog)
            .launchIn(presenterScope)

        billingUseCase.checkSubs()
    }

    override fun onFirstViewAttach() {
        launchOnDefault {
            authRepository.onLogout.collect {
                if (it != 0) {
                    switchToUi { router.newRootScreen(Screen.start()) }
                }
            }
        }
        val appRunCount = settingsRepository.getAppRunCount()
        if (appRunCount == 1)
            analytics.report(AnalyticsEvent.Login.FirstOpen)
    }

    fun launchBilling(activity: Activity, productDetails: ProductDetails, offerDetails: ProductDetails.SubscriptionOfferDetails) {
        billingUseCase.launchBilling(activity, productDetails, offerDetails)
    }

    fun saveSubscription() {
        billingUseCase.onPurchaseSuccess()
    }

    fun setNewRootScreen() = launchOnDefault {
        when {
            !privacyPolicyRepo.checkUserStatus() -> {
                switchToUi { router.newRootScreen(Screen.privacyPolicy()) }
            }
            authRepository.hasAccount() -> {
                analytics.report(AnalyticsEvent.Login.AuthorizedUserEntered)
                accountRepository.getUserEmails()
                switchToUi { router.newRootScreen(Screen.main()) }
            }
            else -> {
                switchToUi { router.newRootScreen(Screen.start()) }
            }
        }
    }

    fun login(code: String, addedAccount: Boolean) {
        viewState.showLoginProgress()

        launchOnDefault(
            CoroutineExceptionHandler { _, throwable ->
                analytics.report(AnalyticsEvent.Login.LoginFailed)
                launchOnMain {
                    viewState.showErrorMessage(throwable)
                    viewState.showLoginButton()
                }

                NonFatalError
                    .log(throwable, ScreenName.START)
                    .setCustomKey(LOGIN, LOGIN_FAILED)
            }
        ) {
            accountRepository.setDonationStatus(false)
            accountRepository.setDonationReminderStatus(true)

            authRepository.login(code)
            val currentUser = accountRepository.getCurrentAccount()
            accountRepository.addAccount(currentUser.toLocalAccount(authRepository.getRefreshToken() ?: ""))
            AppFlags.enableAuthInterceptor = true
            switchToUi { router.newRootScreen(Screen.main()) }

            if (addedAccount)
                analytics.report(AnalyticsEvent.SwitchAccount.SwitchAccountAddAccountSuccess)
            else
                analytics.report(AnalyticsEvent.Login.LoginSuccessfully)
        }
    }

    fun safeLogout() = launchOnDefault { authRepository.safeLogout() }

    fun checkIfUserAddedNewAccount(): Boolean {
        return authRepository.hasAccount()
    }
}
