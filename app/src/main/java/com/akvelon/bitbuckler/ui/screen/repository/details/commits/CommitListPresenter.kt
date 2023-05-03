/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.commits

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.COMMIT_LIST
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.args.CommitDetailsArgs
import com.akvelon.bitbuckler.model.entity.args.CommitsArgs
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.model.repository.BranchRepository
import com.akvelon.bitbuckler.model.repository.RepoRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.pagination.Paginator
import com.akvelon.bitbuckler.ui.pagination.PaginatorRequestState
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
@Suppress("TooManyFunctions")
class CommitListPresenter(
    router: Router,
    val commitsArgs: CommitsArgs,
    private val analytics: AnalyticsProvider,
    private val repoRepository: RepoRepository,
    private val branchRepository: BranchRepository
) : BasePresenter<CommitListView>(router) {

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val response = getCommits(page)
                switchToUi { paginatorRequestState.success(response) }
            }
        },
        object : Paginator.ViewController<Commit> {
            override fun showEmptyProgress(show: Boolean) {
                viewState.showEmptyProgress(show)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                when (error) {
                    is SocketTimeoutException, is UnknownHostException ->
                        viewState.setNoInternetConnectionError(show)
                    is Throwable -> {
                        viewState.setUnknownError(show)
                        NonFatalError.log(error, screenName)
                    }
                }
                viewState.showErrorView(show)
            }

            override fun showEmptyView(show: Boolean) {
                viewState.showEmptyView(show)
            }

            override fun showData(show: Boolean, data: List<Commit>, scrollToTop: Boolean) {
                viewState.showCommits(show, data)
            }

            override fun showErrorMessage(error: Throwable) {
                viewState.showErrorMessage(error)
            }

            override fun showRefreshProgress(show: Boolean) {
                viewState.showRefreshProgress(show)
            }

            override fun showPageProgress(show: Boolean) {
                viewState.showPageProgress(show)
            }

            override fun showPageProgressError(show: Boolean, error: Throwable?) {
                viewState.showPageProgressError(show)
            }
        }
    )

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Commit>> = paginator

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Repository.RepositoryCommitsScreen)

        showBranchName()
        refreshCommits()
    }

    fun refreshCommits() {
        paginator.refresh()
    }

    fun loadNextCommitsPage() {
        paginator.loadNewPage()
    }

    fun onSelectBranchClick() {
        showSelectBranchDialog( false)
    }

    fun onSelectTagClick() {
        showSelectBranchDialog(true)
    }

    fun onCommitClick(commit: Commit) {
        router.navigateTo(
            Screen.commitDetails(
                CommitDetailsArgs(
                    commitsArgs.slugs.workspace,
                    commitsArgs.slugs.repository,
                    commit.hash,
                    commitsArgs.currentBranch
                )
            )
        )
    }

    fun onBranchClick(branch: Branch) {
        commitsArgs.currentBranch = branch.name
        paginator.reset()
        refreshCommits()
        hideBranchDialog()
        showBranchName()
    }

    fun hideBranchDialog() {
        viewState.hideBranchDialog()
    }

    private suspend fun getCommits(page: String?) = with(commitsArgs) {
        repoRepository.getCommitsOfBranch(
            slugs.workspace,
            slugs.repository,
            currentBranch,
            page
        )
    }

    private fun showBranchName() {
        viewState.showBranchName(commitsArgs.currentBranch)
    }

    private fun showSelectBranchDialog(isTags: Boolean) = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain {
                viewState.showBranchDialogProgress(false)
                viewState.showErrorMessage(throwable)
            }
        }
    ) {
        switchToUi { viewState.showBranchDialogProgress(true) }
        val branches = getBranches(isTags)
        switchToUi {
            viewState.showBranchDialogProgress(false)
            viewState.showBranchDialog(branches, isTags)
        }
    }

    private suspend fun getBranches(isTags: Boolean) = getBranchesPage(isTags).values

    private suspend fun getBranchesPage(
        isTags: Boolean,
        page: String? = null): PagedResponse<Branch> {
        var response = fetchResponse(isTags, page)
        var nextPage = response.nextPage

        while (nextPage != null) {
            val tmpResponse = fetchResponse(isTags, nextPage)
            response = PagedResponse(
                size = response.size + tmpResponse.size,
                page = tmpResponse.page,
                pagelen = tmpResponse.pagelen,
                values = response.values.toMutableList().apply {
                    addAll(tmpResponse.values)
                },
                next = tmpResponse.next
            )
            nextPage = tmpResponse.nextPage
        }

        return response
    }

    private suspend fun fetchResponse(isTags: Boolean, page: String ?= null) =
        if(isTags) {
            branchRepository.getTags(
                commitsArgs.slugs.workspace,
                commitsArgs.slugs.repository,
                page
            )
        } else {
            branchRepository.getBranches(
                commitsArgs.slugs.workspace,
                commitsArgs.slugs.repository,
                page
            )
        }

    companion object {
        private const val screenName = COMMIT_LIST
    }
}
