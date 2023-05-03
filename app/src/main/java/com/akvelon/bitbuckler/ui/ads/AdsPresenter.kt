package com.akvelon.bitbuckler.ui.ads

import com.akvelon.bitbuckler.domain.ads.AdsUseCase
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.billing.BillingUseCase
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import moxy.presenterScope

class AdsPresenter(
    router: Router,
    private val adsUseCase: AdsUseCase,
    private val billingUseCase: BillingUseCase,
) : BasePresenter<AdsView>(router) {

    fun checkIfAdsAreEnabled() = launchOnDefault {
        val areAdsEnabled = adsUseCase.areAdsEnabled()
        if (areAdsEnabled) {
            adsUseCase.initAds()
            adsUseCase.setAdsInitialized(true)
        }
    }

    fun requestLoadingAds() {
        billingUseCase.isSubscriptionActive
            .map { isSubscribed ->
                adsUseCase.areAdsEnabled() && adsUseCase.areAdsInitialized() && !isSubscribed
            }.onEach {
                switchToUi { viewState.loadAds(it) }
            }.launchIn(presenterScope)
    }
}