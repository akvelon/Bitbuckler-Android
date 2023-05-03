/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 23 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest

import com.akvelon.bitbuckler.ui.state.screen.PullRequestDetails
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
interface PullRequestDetailsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideBottomNav()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNoInternetConnectionError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setUnknownError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setHaveNoAccessError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showErrorView(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showData(pullRequestDetails: PullRequestDetails)

    @StateStrategyType(SkipStrategy::class)
    fun showError(error: Throwable)

    @StateStrategyType(SkipStrategy::class)
    fun showError(message: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSummary(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showComments()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showBranchDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFullScreenProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showConfirmDeclineDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMergeActionDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showBuildsDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showUsersWhoApproved(users: List<String>)
}
