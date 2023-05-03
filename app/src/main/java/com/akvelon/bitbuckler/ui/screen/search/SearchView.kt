/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 15 December 2021
 */

package com.akvelon.bitbuckler.ui.screen.search

import android.net.Uri
import com.akvelon.bitbuckler.model.entity.search.CodeSearchResult
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
interface SearchView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNoInternetConnectionError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setUnknownError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setSearchIsNotEnabledError(show: Boolean)

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
    fun showResults(show: Boolean, results: List<CodeSearchResult>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showNoResultsView(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun openSearchPage(searchUri: Uri)
}
