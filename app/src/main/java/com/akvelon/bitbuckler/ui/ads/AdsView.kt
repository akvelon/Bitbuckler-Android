package com.akvelon.bitbuckler.ui.ads

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface AdsView: MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun loadAds(activate: Boolean) {}
}