package com.akvelon.bitbuckler.ui.screen.privacypolicy

import android.content.Context
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.AuthRepository
import com.akvelon.bitbuckler.model.repository.PrivacyPolicyRepo
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.navigation.TabTags
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import java.io.File

class PrivacyPolicyPresenter(
    router: Router,
    private val context: Context,
    private val policyRepo: PrivacyPolicyRepo,
    private val analytics: AnalyticsProvider,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
): BasePresenter<PrivacyPolicyView>(router) {

    fun loadWebContent() = launchOnDefault {
        val fileContent = context.assets.open("privacy_policy.html").readBytes().toString(Charsets.UTF_8)
        switchToUi { viewState.loadWebContent(fileContent) }
    }

    fun setUserAccepted() = launchOnDefault {
        analytics.report(AnalyticsEvent.PrivacyPolicy.PrivacyPolicyAccepted)
        policyRepo.updateUserStatus(true)
        when {
            authRepository.hasAccount() -> {
                accountRepository.getUserEmails()
                switchToUi { router.newRootScreen(Screen.main()) }
            }
            else -> {
                switchToUi { router.newRootScreen(Screen.start()) }
            }
        }
    }

    fun onPageClose() = launchOnMain {
        router.backTo(Screen.account(TabTags.ACTIVITY))
    }

    fun screenOpenedEvent() = launchOnDefault {
        analytics.report(AnalyticsEvent.PrivacyPolicy.PrivacyPolicyScreen)
    }

}