/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 29 April 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.list

import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.source.Source
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
interface SourceListView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyError(show: Boolean, error: Throwable?)

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
    fun showFiles(show: Boolean, files: List<Source>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFolderName(folderName: String = "")

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showBranchName(branchName: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showBranchDialogProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showBranchDialog(branches: List<Branch>, isTags: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideBranchDialog()
}
