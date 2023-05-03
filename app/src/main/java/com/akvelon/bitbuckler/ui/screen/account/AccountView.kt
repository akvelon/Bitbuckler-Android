/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.account

import com.akvelon.bitbuckler.model.entity.User
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface AccountView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAccount(account: User)

    @StateStrategyType(SkipStrategy::class)
    fun showError(error: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showConfirm()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideConfirm()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNightModeDialog(currentMode: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDonationStatus()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSubscriptionStatus(isActive: Boolean)
}
