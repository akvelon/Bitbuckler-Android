/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 09 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.project.list

import com.akvelon.bitbuckler.model.entity.Project
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ProjectListView : MvpView {

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
    fun showProjects(show: Boolean, projects: List<Project>, scrollToTop: Boolean)
}
