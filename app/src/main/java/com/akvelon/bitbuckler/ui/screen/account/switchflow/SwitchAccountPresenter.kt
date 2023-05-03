package com.akvelon.bitbuckler.ui.screen.account.switchflow

import android.net.Uri
import com.akvelon.bitbuckler.BuildConfig
import com.akvelon.bitbuckler.common.AppFlags
import com.akvelon.bitbuckler.model.entity.LocalAccount
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.toUser
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.AuthRepository
import com.akvelon.bitbuckler.model.repository.RepoRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.domain.repo.CachedReposUseCase
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.github.terrakok.cicerone.Router
import moxy.InjectViewState

@InjectViewState
class SwitchAccountPresenter(
    router: Router,
    private val analytics: AnalyticsProvider,
    private val authRepository: AuthRepository,
    private val repoRepository: RepoRepository,
    private val cachedReposUseCase: CachedReposUseCase,
    private val accountRepository: AccountRepository
) : BasePresenter<SwitchAccountView>(router) {

    fun logScreenOpenEvent() = launchOnDefault {
        analytics.report(AnalyticsEvent.SwitchAccount.SwitchAccountScreen)
    }

    fun loadCurrentUserAccount() = launchOnDefault {
        val user = accountRepository.getCurrentAccount()
        switchToUi { viewState.onCurrentAccountLoaded(user) }
    }

    fun loadAvailableAccounts() = launchOnDefault {
        val users = accountRepository.getAvailableAccounts()
        switchToUi { viewState.loadExistingAccounts(users) }
    }

    fun setCurrentUser(localAccount: LocalAccount) = launchOnDefault {
        authRepository.setRefreshToken(localAccount.refreshToken)
        accountRepository.setNewUser(localAccount.toUser())
        AppFlags.enableAuthInterceptor = true
        authRepository.updateAccessTokenByRefreshToken()
        analytics.report(AnalyticsEvent.SwitchAccount.SwitchAccountSuccess)
    }

    fun removeUser(user: User, result: () -> Unit) = launchOnDefault {
        accountRepository.removeAccount(user) {
            result.invoke()
        }
        analytics.report(AnalyticsEvent.SwitchAccount.SwitchAccountRemoveAccount)
    }

    fun logCancelEvent() = launchOnDefault {
        analytics.report(AnalyticsEvent.SwitchAccount.SwitchAccountCancel)
    }

    fun safeLogout() = authRepository.safeLogout()

    fun clearAllRecent() = launchOnDefault { repoRepository.clearAllRecent() }

    fun clearAllTracked() = launchOnDefault { cachedReposUseCase.clearTracked() }

    fun loadLoginUri() = launchOnDefault {
        clearAllRecent()
        clearAllTracked()
        cachedReposUseCase.clear()
        AppFlags.enableAuthInterceptor = false

        val authUri = Uri.encode(
            "${BuildConfig.BITBUBCKET_AUTH_API}authorize?client_id=${BuildConfig.CLIENT_ID}&response_type=code"
        )
        val loginUri = Uri.parse(
            "https://id.atlassian.com/login/select-account?continue=$authUri"
        )

        switchToUi { viewState.onLoginPageUriLoaded(loginUri) }
    }
}