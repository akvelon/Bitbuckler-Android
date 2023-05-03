/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 21 March 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.issues.details

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.FOCUS_BEFORE_DESCENDANTS
import android.webkit.WebView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.commitNow
import androidx.recyclerview.widget.LinearLayoutManager.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentIssueDetailsBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.args.CommentsArgs
import com.akvelon.bitbuckler.model.entity.args.IssueArgs
import com.akvelon.bitbuckler.model.entity.issueTracker.Attachment
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueKind
import com.akvelon.bitbuckler.model.entity.repository.issue.IssuePriority
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueState
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.comment.CommentListHolder
import com.akvelon.bitbuckler.ui.screen.comment.list.CommentListFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.issues.details.list.*
import com.akvelon.bitbuckler.ui.state.screen.IssueDetails
import com.akvelon.bitbuckler.ui.view.PreloadLinearLayoutManager
import com.github.terrakok.cicerone.Router
import io.noties.markwon.Markwon
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class IssueDetailsFragment : MvpAppCompatFragment(R.layout.fragment_issue_details), IssueDetailsView, RouterProvider,
    CommentListHolder, BackButtonListener {

    private val binding by viewBinding(FragmentIssueDetailsBinding::bind)

    private val attachmentAdapter = AttachmentsAdapter()

    override val router: Router
        get() = (parentFragment as RouterProvider).router

    val presenter: IssueDetailsPresenter by moxyPresenter {
        get {
            parametersOf((parentFragment as RouterProvider).router, argument(ISSUE_SCOPE_ARG))
        }
    }

    private val markwon: Markwon by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.refreshIssue() }
            }
            errorView.retry.setSafeClickListener { presenter.refreshIssue() }
            attachmentAdapter.setOnItemClickListener {
                val url = it.links.self.href.firstOrNull().orEmpty()
                presenter.requestLoadWebviewPage(url)
            }
            with(rvAttachmentsContent) {
                adapter = attachmentAdapter
                layoutManager = PreloadLinearLayoutManager(requireContext(), null, 0, 0).apply {
                    orientation = HORIZONTAL
                }
                addHorizontalEndlessOnScrollListener {
                    presenter.loadNextFilesPage()
                }
            }
            root.postDelayed({ skeletonLayout.descendantFocusability = FOCUS_BEFORE_DESCENDANTS }, 2000)
        }
        spinnerClickListeners()
    }

    override fun showData(issueDetails: IssueDetails) {
        val issue = issueDetails.issue
        with(binding) {
            tvSubtitle.text = "${getString(R.string.issue)} ${getString(R.string.number_sign, issue.id)}"
            tvTitle.text = issue.title
            updatedOn.text = getString(R.string.updated, issue.updatedOn.timeAgo(requireContext()))
            createdOn.text = getString(R.string.created, issue.createdOn.timeAgo(requireContext()))
            tvReporterName.text = issue.reporter.displayName
            ivReporter.loadCircle(issue.reporter.links.avatar.href)
            ivEdit.setSafeClickListener {
                presenter.getProductDetails()
            }
        }
        showDescription(issue.content)
        showStateDetails(issue)
        showTypeDetails(issue)
        showPriorityDetails(issue)
        setupActionBar(issue)
    }

    private fun showStateDetails(issue: Issue) {
        binding.btStatus.apply {
            val options = IssueState.values()
            adapter = IssueStateSpinnerAdapter(options)
            onItemSelectedListener = null
            setSelection(options.indexOf(issue.state), false)
            postDelayed({
                selected {
                    presenter.updateIssue(status = selectedItem as IssueState)
                }
            }, 1000)
            post {
                isEnabled = presenter.billingUseCase.isSubscriptionActive.value
            }
        }
    }

    private fun showTypeDetails(issue: Issue) {
        binding.btType.apply {
            val options = IssueKind.values()
            adapter = IssueTypeSpinnerAdapter(options)
            onItemSelectedListener = null
            setSelection(options.indexOf(issue.kind), false)
            postDelayed({
                selected {
                    presenter.updateIssue(type = selectedItem as IssueKind)
                }
            }, 1000)
            post {
                isEnabled = presenter.billingUseCase.isSubscriptionActive.value
            }
        }
    }

    private fun showPriorityDetails(issue: Issue) {
        binding.btPriority.apply {
            val options = IssuePriority.values()
            adapter = IssuePrioritySpinnerAdapter(options)
            onItemSelectedListener = null
            setSelection(options.indexOf(issue.priority), false)
            postDelayed({
                selected {
                    presenter.updateIssue(priority = selectedItem as IssuePriority)
                }
            }, 1000)
            post {
                isEnabled = presenter.billingUseCase.isSubscriptionActive.value
            }
        }
    }

    override fun showAssigneeDetails(users: List<User?>) {
        binding.btAssignee.apply {
            adapter = IssueAssigneeSpinnerAdapter(users)
            onItemSelectedListener = null
            setSelection(0, false)
            postDelayed({
                selected {
                    if (selectedItem != null) presenter.changeIssueAssignee(userId = (selectedItem as User).accountId)
                }
            }, 1000)

        }
        with(binding) {
            root.post {
                val enabled = users.size > 1 && presenter.billingUseCase.isSubscriptionActive.value
                ivAssignee.isVisible = enabled
                btAssignee.isEnabled = enabled
            }
        }
    }

    override fun showSubscriptionStatus(isActive: Boolean) {
        with(binding) {
            root.post {
                ivEdit.isVisible = !isActive
                val assigneeEnabled = (btAssignee.adapter?.count ?: 0) > 1 && isActive
                ivAssignee.isVisible = assigneeEnabled
                btAssignee.isEnabled = assigneeEnabled

                btStatus.isEnabled = isActive
                ivStatus.isVisible = isActive

                btPriority.isEnabled = isActive
                ivPriority.isVisible = isActive

                btType.isEnabled = isActive
                ivType.isVisible = isActive
            }
        }
    }

    override fun loadWebViewPage(url: String, headers: Map<String, String>) {
        WebView(requireContext()).loadUrl(url, headers)
    }

    override fun showComments() {
        childFragmentManager.commitNow {
            add(R.id.commentsHolder, CommentListFragment.newInstance(CommentsArgs.fromIssuesArgs(presenter.issueArgs)))
        }
    }

    override fun showEmptyProgress(show: Boolean) {
        with(binding.skeletonLayout) {
            if (show) showSkeleton() else showOriginal()
        }
    }

    override fun showRefreshProgress(show: Boolean) {
        binding.swipeToRefresh.isRefreshing = show
    }

    private fun setupActionBar(issue: Issue) {
        val isIssueOpen = (issue.state == IssueState.NEW || issue.state == IssueState.OPEN)
        binding.btIssueAction.setText(if (isIssueOpen) R.string.resolve else R.string.open)
        binding.btIssueAction.setSafeClickListener {
            presenter.updateIssue(status = if (isIssueOpen) IssueState.RESOLVED else IssueState.OPEN)
        }
    }

    override fun setIssueWatchesAndVotes(votes: Int, watches: Int) {
        binding.votes.text = if (votes == 1) getString(R.string.vote) else getString(R.string.votes, votes)
        binding.watchers.text = if (watches == 1) getString(R.string.watcher) else getString(R.string.watchers, watches)
    }

    override fun isIssueWatched(watched: Boolean) {
        val colorRes = if (watched) R.attr.colorPrimary else R.attr.colorOnBackground
        binding.watchButton.setColorFilter(requireContext().getColorFromStyles(colorRes), PorterDuff.Mode.SRC_IN)
        binding.watchButton.setSafeClickListener {
            presenter.toggleWatchStatus(watched)
        }
    }

    override fun isIssueVoted(voted: Boolean) {
        val colorRes = if (voted) R.attr.colorPrimary else R.attr.colorOnBackground
        binding.voteButton.setColorFilter(requireContext().getColorFromStyles(colorRes), PorterDuff.Mode.SRC_IN)
        binding.voteButton.setSafeClickListener {
            presenter.toggleVoteStatus(voted)
        }
    }

    override fun showFiles(show: Boolean, data: List<Attachment>) {
        with(binding.tvAttachmentsNoContent) {
            setTextAppearance(R.style.TextAppearance_AppCompat_Small)
            setItalicTypeface()
            isVisible = !show
        }
        binding.rvAttachmentsContent.isVisible = show
        attachmentAdapter.updateData(data)
    }

    private fun spinnerClickListeners() {
        binding.ivAssignee.setSafeClickListener {
            binding.btAssignee.performClick()
        }
        binding.ivStatus.setSafeClickListener {
            binding.btStatus.performClick()
        }
        binding.ivType.setSafeClickListener {
            binding.btType.performClick()
        }
        binding.ivPriority.setSafeClickListener {
            binding.btPriority.performClick()
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

    override fun setNoInternetConnectionError(show: Boolean) {
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot_cry)
            errorTitle.text = getString(R.string.no_internet_title)
            errorSubtitle.text = getString(R.string.no_internet_subtitle)
        }
    }

    override fun setUnknownError(show: Boolean) {
        with(binding.errorView) {
            errorImage?.setImageResource(R.drawable.ic_robot)
            errorTitle.text = getString(R.string.error_list_title)
            errorSubtitle.text = getString(R.string.error_list_subtitle)
        }
    }

    override fun showErrorView(show: Boolean) {
        binding.errorView.root.setVisible(show)
        binding.swipeToRefresh.isEnabled = !show
    }

    override fun showFullScreenProgress(show: Boolean) {
        binding.fullScreenProgress.root.setVisible(show)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    private fun showDescription(content: Content?) {
        with(binding.tvDescriptionContent) {
            if (content.isNullOrEmpty()) {
                text = getString(R.string.no_description)
                setTextAppearance(R.style.TextAppearance_AppCompat_Small)
                setItalicTypeface()
            } else {
                content?.let { markwon.setMarkdown(this, it.raw) }
            }
        }
    }

    companion object {
        const val ISSUE_SCOPE_ARG = "issue_scope_arg"

        fun newInstance(issueArgs: IssueArgs) = IssueDetailsFragment().apply {
            arguments = bundleOf(ISSUE_SCOPE_ARG to issueArgs)
        }
    }

    override fun showDeleteCommentProgress(show: Boolean) {
        showFullScreenProgress(show)
    }
}
