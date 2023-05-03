/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.start

import android.net.Uri
import com.akvelon.bitbuckler.BuildConfig
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.github.terrakok.cicerone.Router
import moxy.InjectViewState

@InjectViewState
class StartPresenter(
    router: Router,
    private val analytics: AnalyticsProvider,
) : BasePresenter<StartView>(router) {

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Login.LoginScreen)
    }

    fun onLoginPressed() {
        val authUri = Uri.encode(
            "${BuildConfig.BITBUBCKET_AUTH_API}authorize?client_id=${BuildConfig.CLIENT_ID}&response_type=code"
        )
        val loginUri = Uri.parse(
            "https://id.atlassian.com/login/select-account?continue=$authUri"
        )

        analytics.report(AnalyticsEvent.Login.LoginButtonClicked)

        viewState.openLoginPage(loginUri)
    }

    fun showLoginButton() {
        viewState.showLoginButton()
    }

    fun onBrowserNotFound() {

    }

}
