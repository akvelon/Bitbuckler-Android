/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.details

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentSourceDetailsBinding
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.hide
import com.akvelon.bitbuckler.extension.nightModeOn
import com.akvelon.bitbuckler.extension.setVisible
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.model.entity.args.SourceDetailsArgs
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.navigation.SlideFragmentTransition
import com.caverock.androidsvg.SVG
import com.pddstudio.highlightjs.models.Theme
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import java.io.IOException
import java.lang.IllegalArgumentException

class SourceDetailsFragment :
    MvpAppCompatFragment(R.layout.fragment_source_details),
    SourceDetailsView,
    SlideFragmentTransition,
    BackButtonListener {

    private val binding by viewBinding(FragmentSourceDetailsBinding::bind)

    val presenter: SourceDetailsPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(SOURCE_DETAILS_ARGS)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
            errorEmptyView.retry.setOnClickListener { presenter.refresh() }
        }
    }

    override fun showFileName(fileName: String) {
        binding.toolbar.title = fileName
    }

    override fun showFileContent(content: String) = with(binding) {
        val contentTheme = if (requireContext().nightModeOn()) Theme.DARKULA else Theme.ATOM_ONE_LIGHT

        fileContent.apply {
            theme = contentTheme
            setShowLineNumbers(true)
            setSource(content)
            show()
        }

        imageContent.hide()
    }

    override fun showImageContent(bitmap: Bitmap) = with(binding) {
        fileContent.hide()

        imageContent.setImageBitmap(bitmap)
        imageContent.show()
    }

    override fun showImageContent(svg: SVG) = with(binding) {
        fileContent.hide()

        try {
            val bitmap = Bitmap.createBitmap(
                svg.documentWidth.toInt(),
                svg.documentHeight.toInt(),
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            svg.renderToCanvas(canvas, svg.documentViewBox)

            imageContent.setImageBitmap(bitmap)
            imageContent.show()
        } catch (e: IllegalArgumentException) {
            showPreviewUnavailable(true)
        }
    }

    override fun showProgressBar(show: Boolean) {
        binding.progressEmptyView.root.setVisible(show)
    }

    override fun showEmptyError(show: Boolean, error: Throwable?) {
        when (error) {
            is IOException -> showNoInternetConnectionError()
            is Throwable -> showUnknownError()
        }

        binding.errorEmptyView.root.setVisible(show)
    }

    override fun showPreviewUnavailable(show: Boolean) {
        binding.previewUnavailable.root.setVisible(show)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    private fun showNoInternetConnectionError() = with(binding.errorEmptyView) {
        errorImage?.setImageResource(R.drawable.ic_robot_cry)
        errorTitle.text = getString(R.string.no_internet_title)
        errorSubtitle.text = getString(R.string.no_internet_subtitle)
    }

    private fun showUnknownError() = with(binding.errorEmptyView) {
        errorImage?.setImageResource(R.drawable.ic_robot)
        errorTitle.text = getString(R.string.error_list_title)
        errorSubtitle.text = getString(R.string.error_list_subtitle)
    }

    companion object {
        const val SOURCE_DETAILS_ARGS = "source_details_args"

        fun newInstance(sourceDetailsArgs: SourceDetailsArgs) = SourceDetailsFragment().apply {
            arguments = bundleOf(
                SOURCE_DETAILS_ARGS to sourceDetailsArgs
            )
        }
    }
}
