/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 08 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.main

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MainView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showBottomNav()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideBottomNav()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showRateDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDonationDialog()
}
