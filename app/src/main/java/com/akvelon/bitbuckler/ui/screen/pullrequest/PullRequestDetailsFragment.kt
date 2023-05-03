/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 23 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentPullRequestDetailsBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.args.CommentsArgs
import com.akvelon.bitbuckler.model.entity.args.MergeActionArgs
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeStrategy
import com.akvelon.bitbuckler.model.entity.statuses.Status
import com.akvelon.bitbuckler.model.entity.statuses.StatusState
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.comment.CommentListHolder
import com.akvelon.bitbuckler.ui.screen.comment.list.CommentListFragment
import com.akvelon.bitbuckler.ui.screen.dialog.branch.BranchDialogFragment
import com.akvelon.bitbuckler.ui.screen.dialog.builds.BuildsDialogFragment
import com.akvelon.bitbuckler.ui.screen.dialog.decline.DeclineActionDialogFragment
import com.akvelon.bitbuckler.ui.screen.dialog.merge.MergeActionDialogFragment
import com.akvelon.bitbuckler.ui.screen.pullrequest.adapter.ApprovedByAdapter
import com.akvelon.bitbuckler.ui.screen.pullrequest.adapter.PullRequestReviewersAdapter
import com.akvelon.bitbuckler.ui.state.screen.PullRequestDetails
import com.faltenreich.skeletonlayout.applySkeleton
import com.github.terrakok.cicerone.Router
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class PullRequestDetailsFragment :
    MvpAppCompatFragment(R.layout.fragment_pull_request_details),
    PullRequestDetailsView,
    RouterProvider,
    CommentListHolder,
    BackButtonListener,
    MergeActionDialogFragment.MergeActionDialogListener,
    DeclineActionDialogFragment.DeclineActionDialogListener {

    private val binding by viewBinding(FragmentPullRequestDetailsBinding::bind)

    override val router: Router
        get() = (parentFragment as RouterProvider).router

    val presenter: PullRequestDetailsPresenter by moxyPresenter {
        get {
            parametersOf(
                router,
                argument(PULL_REQUEST_SCOPE_ARG)
            )
        }
    }

    private val markwon: Markwon by inject()

    private var branchDialog: BranchDialogFragment? = null

    private var buildsDialog: BuildsDialogFragment? = null

    private var mergeActionDialog: MergeActionDialogFragment? = null

    private val declineActionDialog by lazy { DeclineActionDialogFragment() }

    private val rotateCounterclockwise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_counter_clockwise)
    }
    private val rotateClockwise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_clockwise)
    }
    private val adapter: ApprovedByAdapter by lazy { ApprovedByAdapter() }
    private var isSummaryExpanded = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { refreshPullRequest() }
            }

            toggleSummary.setOnClickListener { presenter.showSummary(!summary.isVisible) }

            recyclerViewReviewers.setHasFixedSize(true)

            pullRequestActions.apply {
                setOnMergeClickListener { presenter.showMergeActionDialog() }
                setOnDeclineClickListener { presenter.showDeclineActionDialog() }

                onApproveClickListener = {
                    presenter.approve()
                    launchShowUserWhoApproved()
                }
                onRevokeApproveClickListener = {
                    presenter.revokeApproval()
                    launchShowUserWhoApproved()
                }

                onRequestChangesClickListener = { presenter.requestChanges() }
                onRevokeRequestChangesClickListener = { presenter.revokeRequestChanges() }
            }

            errorView.retry.setOnClickListener { refreshPullRequest() }
        }

        setupApprovedByRecyclerView()
    }

    override fun hideBottomNav() {
        (parentFragment as BottomNavListener).setBottomNavVisibility(false)
    }

    override fun showEmptyProgress(show: Boolean) {
        with(binding) {
            if (show) {
                skeletonLayout.showSkeleton()

                recyclerViewReviewers.applySkeleton(R.layout.item_reviewer).showSkeleton()
            } else {
                skeletonLayout.showOriginal()
            }
        }
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    override fun showData(pullRequestDetails: PullRequestDetails) {
        showPullRequest(pullRequestDetails.pullRequest)

        showBuilds(
            pullRequestDetails.builds.sortedWith(
                compareBy<Status> { it.state.order }.thenByDescending { it.name }
            )
        )

        with(binding) {
            branchDialog = BranchDialogFragment.newInstance(
                pullRequestDetails.pullRequest.sourceBranch,
                pullRequestDetails.pullRequest.destinationBranch
            )
            container.post {
                branchGroup.setAllOnClickListener {
                    presenter.showBranchDialog()
                }
            }

            if (pullRequestDetails.pullRequest.isOpen) {
                mergeActionDialog = MergeActionDialogFragment.newInstance(
                    MergeActionArgs.fromPullRequest(pullRequestDetails.pullRequest)
                )

                pullRequestActions.showActions(
                    pullRequestDetails.pullRequest,
                    pullRequestDetails.account
                )
            } else {
                pullRequestActions.hide()
            }
        }
    }

    override fun showError(error: Throwable) {
        binding.swipeToRefresh.isRefreshing = false
        requireView().snackError(error)
    }

    override fun showError(message: String) {
        binding.swipeToRefresh.isRefreshing = false
        requireView().snack(message)
    }

    override fun showSummary(show: Boolean) {
        isSummaryExpanded = show
        with(binding) {
            TransitionManager.beginDelayedTransition(
                summary.rootView as ViewGroup,
                AutoTransition()
            )

            if (show) {
                toggleSummary.startAnimation(rotateClockwise)
                launchShowUserWhoApproved()
            } else {
                toggleSummary.startAnimation(rotateCounterclockwise)
                hideApprovedStatus()
            }

            summary.setVisible(show)
        }
    }

    override fun showComments() {
        childFragmentManager.commitNow {
            add(
                R.id.commentsContainer,
                CommentListFragment.newInstance(
                    CommentsArgs.fromPullRequestArgs(presenter.pullRequestArgs)
                )
            )
        }
    }

    override fun setNoInternetConnectionError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot_cry)
            errorTitle.text = getString(R.string.no_internet_title)
            errorSubtitle.text = getString(R.string.no_internet_subtitle)
        }

    override fun setUnknownError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.error_list_title)
            errorSubtitle.text = getString(R.string.error_list_subtitle)
        }

    override fun setHaveNoAccessError(show: Boolean) =
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.no_access_title)
            errorSubtitle.text = getString(R.string.no_access_subtitle)
        }

    override fun showErrorView(show: Boolean) {
        binding.errorView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showBranchDialog() {
        branchDialog?.show(childFragmentManager, BranchDialogFragment.TAG)
    }

    override fun showBuildsDialog() {
        buildsDialog?.show(childFragmentManager, BuildsDialogFragment.TAG)
    }

    override fun showUsersWhoApproved(users: List<String>) = with(binding) {
        if (users.isEmpty()) {
            toggleApprovedByListVisibility(false)
            return@with
        }
        toggleApprovedByListVisibility(true)
        adapter.submitList(users)
    }

    override fun showFullScreenProgress(show: Boolean) {
        binding.fullScreenProgress.root.setVisible(show)
        binding.pullRequestActions.isEnabledActions = !show
    }

    override fun showConfirmDeclineDialog() {
        declineActionDialog.show(
            childFragmentManager,
            DeclineActionDialogFragment.TAG
        )
    }

    override fun showMergeActionDialog() {
        mergeActionDialog?.show(childFragmentManager, MergeActionDialogFragment.TAG)
    }

    override fun showDeleteCommentProgress(show: Boolean) {
        showFullScreenProgress(show)
    }

    override fun mergeAction(
        message: String,
        closedSourceBranch: Boolean,
        mergeStrategy: MergeStrategy,
    ) {
        presenter.merge(message, closedSourceBranch, mergeStrategy)
    }

    override fun declineAction() {
        presenter.decline()
    }

    override fun onBackPressed() {
        requireActivity().intent.data = null
        presenter.onBackPressed()
    }

    private fun refreshPullRequest() {
        (childFragmentManager.fragments.first() as? CommentListFragment)?.refreshComments()
        presenter.refreshPullRequest()
        launchShowUserWhoApproved()
    }

    private fun showPullRequest(pullRequest: PullRequest) = with(binding) {
        title.text = pullRequest.title
        avatar.loadCircle(pullRequest.author.links.avatar.href)
        author.text = pullRequest.author.displayName

        calculateBranchesNameWidth(pullRequest.sourceBranch, pullRequest.destinationBranch)

        status.apply {
            text = pullRequest.state.toString()
            setTextColor(getColor(pullRequest.state.getStateTextColor()))
            setBackgroundResource(pullRequest.state.getStateBackground())
        }

        createdOn.text = createdOn.getString(
            R.string.created,
            pullRequest.createdOn.timeAgo(requireContext())
        )
        updatedOn.text = createdOn.getString(
            R.string.updated,
            pullRequest.updatedOn.timeAgo(requireContext())
        )

        toFiles.setOnClickListener { presenter.onFilesClick(pullRequest.title) }
        toCommits.setOnClickListener { presenter.onCommitsClick(pullRequest.title) }

        pullRequest.reviewers?.let {
            recyclerViewReviewers.adapter = PullRequestReviewersAdapter(it)
        }

        showDescription(pullRequest.summary)
    }

    private fun calculateBranchesNameWidth(sourceBranchText: String, targetBranchText: String) = with(binding) {
        val sourceTextLength = sourceBranchText.length
        val targetTextLength = targetBranchText.length
        if (sourceTextLength < BRANCH_VIEWS_MAX_LETTERS && targetTextLength < BRANCH_VIEWS_MAX_LETTERS) {
            sourceBranch.setWidthToWrapContent()
            targetBranch.setWidthToWrapContent()
        } else if (BRANCH_VIEWS_MAX_LETTERS in targetTextLength..sourceTextLength) {
            targetBranch.setWidthToWrapContent()
        } else if (BRANCH_VIEWS_MAX_LETTERS in sourceTextLength..targetTextLength) {
            sourceBranch.setWidthToWrapContent()
        }
        sourceBranch.text = sourceBranchText
        targetBranch.text = targetBranchText
    }

    private fun showDescription(content: Content?) = with(binding.description) {
        if (content.isNullOrEmpty()) {
            text = getString(R.string.no_description)
            setTextAppearance(R.style.TextAppearance_AppCompat_Small)
            setItalicTypeface()
        } else {
            content?.let { markwon.setMarkdown(this, it.raw) }
        }
    }

    private fun showBuilds(builds: List<Status>) {
        with(binding.buildsStatus) {
            if (builds.isEmpty()) {
                progressGroup.hide()
                emptyBuilds.show()

                return
            }

            var countSuccessful = 0
            var countFailed = 0
            var countInProgress = 0
            var countStopped = 0

            builds.forEach { build ->
                when (build.state) {
                    StatusState.SUCCESSFUL -> countSuccessful++
                    StatusState.FAILED -> countFailed++
                    StatusState.INPROGRESS -> countInProgress++
                    StatusState.STOPPED -> countStopped++
                }
            }

            buildCount.text = buildCount.getString(
                R.string.count_of,
                countSuccessful.toString(),
                builds.count().toString()
            )

            buildsProgress.setCounts(countSuccessful, countFailed, countInProgress, countStopped)

            buildsDialog = BuildsDialogFragment.newInstance(builds)
            root.setOnClickListener { presenter.showBuildsDialog() }

            progressGroup.show()
        }
    }

    private fun setupApprovedByRecyclerView() = with(binding) {
        val layoutManger = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        rvApprovedBy.layoutManager = layoutManger
        rvApprovedBy.adapter = adapter

        launchShowUserWhoApproved()
    }

    private fun toggleApprovedByListVisibility(isVisible: Boolean) = with(binding) {
        if (!isSummaryExpanded) return@with
        val constraintSet = ConstraintSet()
        constraintSet.clone(container)
        if (isVisible) {
            constraintSet.connect(
                R.id.descriptionTitle,
                ConstraintSet.TOP,
                R.id.rv_approved_by,
                ConstraintSet.BOTTOM
            )
        } else {
            constraintSet.connect(
                R.id.descriptionTitle,
                ConstraintSet.TOP,
                R.id.tv_not_approved_yet,
                ConstraintSet.BOTTOM
            )
        }
        constraintSet.applyTo(container)
        tvNotApprovedYet.setVisible(!isVisible)
        rvApprovedBy.setVisible(isVisible)
    }

    private fun launchShowUserWhoApproved() =
        lifecycleScope.launch { presenter.showUsersWhoApproved() }

    private fun hideApprovedStatus() = with(binding) {
        rvApprovedBy.setVisible(false)
        tvNotApprovedYet.setVisible(false)
    }

    companion object {
        const val PULL_REQUEST_SCOPE_ARG = "pull_request_scope_arg"
        const val BRANCH_VIEWS_MAX_LETTERS = 10

        fun newInstance(pullRequestArgs: PullRequestArgs) = PullRequestDetailsFragment().apply {
            arguments = bundleOf(
                PULL_REQUEST_SCOPE_ARG to pullRequestArgs
            )
        }
    }
}
