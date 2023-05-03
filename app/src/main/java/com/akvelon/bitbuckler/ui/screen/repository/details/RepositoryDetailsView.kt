/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details

import com.akvelon.bitbuckler.model.entity.repository.Repository
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface RepositoryDetailsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNoInternetConnectionError()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNoAccessError()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setUnknownError()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showErrorView(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showData(repository: Repository)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showReadme(content: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideReadme()

    @StateStrategyType(SkipStrategy::class)
    fun showError(error: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRefreshingProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun toggleDescription(collapse: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLimitTrackedRepoError()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setRepoTracked(flag: Boolean)

}
