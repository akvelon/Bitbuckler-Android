/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 4 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.details

import android.graphics.Bitmap
import com.caverock.androidsvg.SVG
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SourceDetailsView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFileName(fileName: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFileContent(content: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showImageContent(bitmap: Bitmap)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showImageContent(svg: SVG)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressBar(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyError(show: Boolean, error: Throwable? = null)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPreviewUnavailable(show: Boolean)
}
