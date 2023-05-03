package com.akvelon.bitbuckler.ui.screen.repository

import android.content.SharedPreferences
import com.akvelon.bitbuckler.model.datasource.local.LocalFlags.TRACK_REPOS_FLAG
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router

class TrackingInfoPresenter(
    router: Router,
    private val analytics: AnalyticsProvider,
    private val sharedPreferences: SharedPreferences,
) : BasePresenter<TrackingInfoView>(router) {

    fun setTrackReposFlag(flag: Boolean) = launchOnDefault {
        sharedPreferences.edit().putBoolean(TRACK_REPOS_FLAG, flag).apply()
    }

    fun trackReposPopupOpened() = launchOnDefault {
        analytics.report(AnalyticsEvent.ChooseRepositoriesPopUp.ChooseReposPopUpScreen)
    }

    fun trackReposDoNotShowAgainAction(flag: Boolean) = launchOnDefault {
        if (flag)
            analytics.report(AnalyticsEvent.ChooseRepositoriesPopUp.ChooseReposCancelNoRepeat)
    }

    fun trackReposCancelAction() = launchOnDefault {
        analytics.report(AnalyticsEvent.ChooseRepositoriesPopUp.ChooseReposCancelRepeat)
    }

    fun trackReposConfirmClicked() = launchOnDefault {
        analytics.report(AnalyticsEvent.ChooseRepositoriesPopUp.ChooseReposChoose)
    }

    fun navigateToTrackRepoScreen() {
        router.navigateTo(Screen.trackRepoSelection())
    }
}