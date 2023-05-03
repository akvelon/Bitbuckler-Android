package com.akvelon.bitbuckler.ui.screen.activity.dialog

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface PrParseView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onPrParseError(message: String) {}

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onPrParseSuccess() {}
}