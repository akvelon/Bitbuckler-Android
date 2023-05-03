/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest.commits

import com.akvelon.bitbuckler.model.entity.repository.Commit
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
interface PrCommitListView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNoInternetConnectionError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setUnknownError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showErrorView(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyView(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(error: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPageProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPageProgressError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCommits(show: Boolean, commits: List<Commit>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideBottomNav()
}
