/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.details

import android.graphics.BitmapFactory
import com.akvelon.bitbuckler.model.entity.args.SourceDetailsArgs
import com.akvelon.bitbuckler.model.entity.source.ContentType
import com.akvelon.bitbuckler.model.repository.SourceRepository
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import moxy.InjectViewState
import okhttp3.ResponseBody
import java.lang.IllegalArgumentException

@InjectViewState
class SourceDetailsPresenter(
    router: Router,
    private val sourceDetailsArgs: SourceDetailsArgs,
    private val sourceRepository: SourceRepository
) : BasePresenter<SourceDetailsView>(router) {

    override fun onFirstViewAttach() {
        viewState.showFileName(sourceDetailsArgs.fileName)
        viewState.showProgressBar(true)
        getFileContent()
    }

    fun refresh() {
        viewState.showEmptyError(false)
        viewState.showProgressBar(true)
        getFileContent()
    }

    private fun getFileContent() {
        with(sourceDetailsArgs) {
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain {
                        viewState.showProgressBar(false)
                        viewState.showEmptyError(true, throwable)
                    }
                }
            ) {
                val result = sourceRepository.getFileContent(
                    slugs.workspace,
                    slugs.repository,
                    commitHash,
                    path
                )

                switchToUi {
                    result.contentType()?.let { contentType ->
                        when (contentType.type) {
                            ContentType.TEXT -> {
                                asyncOnDefault {
                                    val resultString = result.string()
                                    switchToUi { viewState.showFileContent(resultString) }
                                }
                            }
                            ContentType.IMAGE -> {
                                if (contentType.subtype == SVG_SUBTYPE) { showSvgContent(result) }
                                else { showImageContent(result) }
                            }
                            else -> viewState.showPreviewUnavailable(true)
                        }
                    }
                }
                delay(500)
                switchToUi { viewState.showProgressBar(false) }
            }
        }
    }

    private fun showSvgContent(response: ResponseBody) {
        try {
            viewState.showImageContent(SVG.getFromString(response.string()))
        } catch (e: SVGParseException) {
            viewState.showPreviewUnavailable(true)
        }
    }

    private fun showImageContent(response: ResponseBody) {
        try {
            viewState.showImageContent(BitmapFactory.decodeStream(response.byteStream()))
        } catch (e: IllegalArgumentException) {
            viewState.showPreviewUnavailable(true)
        }
    }

    companion object {
        const val SVG_SUBTYPE = "svg+xml"
    }
}
