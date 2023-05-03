/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentRepositoryDetailsBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.ui.ads.AdsPresenter
import com.akvelon.bitbuckler.ui.ads.AdsView
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.view.ads.BitbucklerAdsView
import com.akvelon.bitbuckler.ui.view.ads.customizeAndAddAdView
import com.google.android.gms.ads.AdView
import io.noties.markwon.Markwon
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

@Suppress("TooManyFunctions")
class RepositoryDetailsFragment :
    MvpAppCompatFragment(R.layout.fragment_repository_details),
    RepositoryDetailsView,
    AdsView,
    BackButtonListener {

    private val binding by viewBinding(FragmentRepositoryDetailsBinding::bind)

    val presenter: RepositoryDetailsPresenter by moxyPresenter {
        get {
            parametersOf(
                (parentFragment as RouterProvider).router,
                argument(SLUGS_ARG)
            )
        }
    }

    private val adsPresenter: AdsPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    private val markwon: Markwon by inject()

    private var motionState: Bundle? = null

    private var isInitialTrackSetup = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsPresenter.requestLoadingAds()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            navigationButton.setOnClickListener { onBackPressed() }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshRepository() }
            }

            motionLayout.apply {
                doOnTransitionChange { _, _, _, progress ->
                    swipeToRefresh.isEnabled = progress == 0f
                }

                savedInstanceState?.getBundle(MOTION_LAYOUT_STATE)?.let {
                    transitionState = it
                }
            }

            branchesSection.setOnClickListener { presenter.onBranchesClick() }
            pullRequestsSection.setOnClickListener { presenter.onPullRequestClick() }
            issuesSection.setOnClickListener { presenter.onIssuesClick() }

            errorView.root.setOnTouchListener { _, _ -> true }
            emptyProgress.root.setOnTouchListener { _, _ -> true }

            errorView.retry.setOnClickListener { presenter.refreshRepository() }
        }
    }

    override fun showEmptyProgress(show: Boolean) {
        binding.emptyProgress.root.setVisible(show)
    }

    override fun setNoInternetConnectionError() =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot_cry)
            errorTitle.text = getString(R.string.no_internet_title)
            errorSubtitle.text = getString(R.string.no_internet_subtitle)
        }

    override fun setNoAccessError() =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_stop)
            errorTitle.text = getString(R.string.no_access_title)
            errorSubtitle.text = getString(R.string.no_access_subtitle)
        }

    override fun setUnknownError() =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.error_list_title)
            errorSubtitle.text = getString(R.string.error_list_subtitle)
        }

    override fun showErrorView(show: Boolean) {
        binding.errorView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showData(repository: Repository) {
        presenter.save(repository.toRecent())

        with(binding) {
            name.text = repository.name

            description.text = repository.description
            if (repository.description.isNotEmpty()) {
                description.setOnClickListener { presenter.toggleDescription() }
            }

            avatar.loadSquare(repository.links.avatar.href)

            if (repository.language.isNotEmpty()) {
                language.text = repository.language.upperFirst(Locale.getDefault())
            } else {
                languageBadge.hide()
            }

            size.text = repository.size.toHumanBytesCount()

            isPrivate.text = getString(repository.getIsPrivateTitle())
            isPrivateImage.setImageResource(repository.getIsPrivateIconId())

            updatedOn.text = repository.updatedOn.timeAgo(requireContext())

            commitsSection.apply {
                setVisible(repository.mainBranch != null)
                setOnClickListener { presenter.onCommitsClick(repository) }
            }

            sourceSection.apply {
                setVisible(repository.mainBranch != null)
                setOnClickListener { presenter.onSourceClick(repository) }
            }

            issuesSection.setVisible(repository.hasIssues)

            trackedButton.setOnCheckedChangeListener { _, state ->
                if (isInitialTrackSetup) {
                    isInitialTrackSetup = false
                } else {
                    if (state) {
                        presenter.trackRepository()
                    } else {
                        presenter.unTrackRepository()
                    }
                }
            }
            if (!repository.isTracked) {
                isInitialTrackSetup = false
            }
            trackedButton.isChecked = repository.isTracked
        }
    }

    override fun showReadme(content: String) =
        with(binding) {
            readmeGroup.show()
            markwon.setMarkdown(readmeContent, content)

            readmeProgress.hide()
        }

    override fun hideReadme() =
        with(binding) {
            readmeGroup.hide()
            readmeProgress.hide()
        }

    override fun showError(error: Throwable) {
        binding.swipeToRefresh.isRefreshing = false
        requireView().snackError(error)
    }

    override fun showRefreshingProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun toggleDescription(collapse: Boolean) = with(binding) {
        TransitionManager.beginDelayedTransition(
            description.rootView as ViewGroup,
            AutoTransition()
        )

        if (collapse) {
            description.maxLines = DESCRIPTION_MAX_LINE
        } else {
            description.maxLines = Integer.MAX_VALUE
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onPause() {
        super.onPause()

        motionState = binding.motionLayout.transitionState
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        motionState?.let {
            outState.putBundle(MOTION_LAYOUT_STATE, it)
        }
    }

    override fun showLimitTrackedRepoError() {
        requireContext().showTrackLimitError(requireActivity())
    }

    override fun setRepoTracked(flag: Boolean) {
        with(binding.trackedButton) {
            isChecked = flag
        }
    }

    override fun loadAds(activate: Boolean) {
        if (activate) {
            BitbucklerAdsView(requireContext(), BitbucklerAdsView.ADMOB)
                .getCurrentAdView()
                .customizeAndAddAdView(binding.root)
        } else {
            binding.root.children.find { it is AdView }?.let {
                val adView = it as AdView
                adView.destroy()
                adView.isVisible = false
                binding.root.removeView(adView)
            }
        }
    }

    companion object {
        const val SLUGS_ARG = "slugs_arg"

        const val MOTION_LAYOUT_STATE = "motion_layout_state"

        const val DESCRIPTION_MAX_LINE = 3

        fun newInstance(slugs: Slugs) = RepositoryDetailsFragment().apply {
            arguments = bundleOf(
                SLUGS_ARG to slugs
            )
        }
    }
}
