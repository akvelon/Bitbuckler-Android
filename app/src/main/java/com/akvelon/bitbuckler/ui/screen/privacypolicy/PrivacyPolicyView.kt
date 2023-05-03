package com.akvelon.bitbuckler.ui.screen.privacypolicy

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface PrivacyPolicyView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun loadWebContent(data: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun enableAcceptButton()
}