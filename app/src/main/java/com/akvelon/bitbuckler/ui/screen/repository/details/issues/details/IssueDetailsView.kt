/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 21 March 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.issues.details

import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.issueTracker.Attachment
import com.akvelon.bitbuckler.ui.state.screen.IssueDetails
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface IssueDetailsView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNoInternetConnectionError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setUnknownError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showErrorView(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showData(issueDetails: IssueDetails)

    @StateStrategyType(SkipStrategy::class)
    fun showError(error: Throwable)

    @StateStrategyType(SkipStrategy::class)
    fun showError(message: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFullScreenProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showComments()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFiles(show: Boolean, data: List<Attachment>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun loadWebViewPage(url: String, headers: Map<String, String>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun isIssueWatched(watched: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun isIssueVoted(voted: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setIssueWatchesAndVotes(votes:Int, watches:Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAssigneeDetails(users:List<User?>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSubscriptionStatus(isActive: Boolean)
}
