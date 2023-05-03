/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.activity

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ActivityListView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun reflectPremiumStatus(isPremium: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showTrackReposPage(show: Boolean)
}
