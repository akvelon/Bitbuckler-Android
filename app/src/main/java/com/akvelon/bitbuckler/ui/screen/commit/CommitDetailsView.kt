/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.commit

import com.akvelon.bitbuckler.ui.state.screen.CommitDetails
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
interface CommitDetailsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideBottomNav()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNoInternetConnectionError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setUnknownError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showErrorView(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showData(commitDetails: CommitDetails)

    @StateStrategyType(SkipStrategy::class)
    fun showError(error: Throwable)

    @StateStrategyType(SkipStrategy::class)
    fun showError(message: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showComments()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFullScreenProgress(show: Boolean)
}
