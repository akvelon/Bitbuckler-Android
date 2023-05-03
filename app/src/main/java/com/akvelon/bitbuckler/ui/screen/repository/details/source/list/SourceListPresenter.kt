/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 29 April 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.list

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.args.SourceArgs
import com.akvelon.bitbuckler.model.entity.args.SourceDetailsArgs
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.source.Source
import com.akvelon.bitbuckler.model.entity.source.SourceType
import com.akvelon.bitbuckler.model.repository.BranchRepository
import com.akvelon.bitbuckler.model.repository.RepoRepository
import com.akvelon.bitbuckler.model.repository.SourceRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.pagination.Paginator
import com.akvelon.bitbuckler.ui.pagination.PaginatorRequestState
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import java.lang.Exception

@Suppress("TooManyFunctions")
@InjectViewState
class SourceListPresenter(
    router: Router,
    val sourceArgs: SourceArgs,
    private val analytics: AnalyticsProvider,
    private val sourceRepository: SourceRepository,
    private val repoRepository: RepoRepository,
    private val branchRepository: BranchRepository
) : BasePresenter<SourceListView>(router) {

    private var currentCommitHash: String? = null

    private val paginator = Paginator(
        { page ->
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { paginatorRequestState.failure(throwable) }
                }
            ) {
                val response = getFiles(page)
                switchToUi { paginatorRequestState.success(response) }
            }
        },
        object : Paginator.ViewController<Source> {
            override fun showEmptyProgress(show: Boolean) {
                viewState.showEmptyProgress(show)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                viewState.showEmptyError(show, error)
            }

            override fun showEmptyView(show: Boolean) {
                viewState.showEmptyView(show)
            }

            override fun showData(show: Boolean, data: List<Source>, scrollToTop: Boolean) {
                viewState.showFiles(show, data)
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

    var paginatorRequestState: PaginatorRequestState<PagedResponse<Source>> = paginator

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Repository.RepositorySourceScreen)

        showBranchName()
        showFolderName()
        refreshFiles()
    }

    fun refreshFiles() {
        currentCommitHash = null
        paginator.refresh()
    }

    fun onSourceClick(source: Source) {
        when (source.type) {
            SourceType.COMMIT_DIRECTORY -> {
                sourceArgs.folders.add(source.name)
                viewState.showFolderName(source.name)
                hardRefreshFiles()
            }
            SourceType.COMMIT_FILE -> {
                val args = SourceDetailsArgs(
                    sourceArgs.slugs,
                    source.commit.hash,
                    source.path
                )
                router.navigateTo(Screen.sourceDetails(args))
            }
        }
    }

    fun toPreviousFolderClick() {
        if (sourceArgs.folders.isNotEmpty()) {
            sourceArgs.folders.removeLast()
            showFolderName()
            hardRefreshFiles()
        }
    }

    fun loadNextFilesPage() {
        paginator.loadNewPage()
    }

    fun onSelectBranchClick() {
        showSelectBranchDialog(false)
    }

    fun onSelectTagClick() {
        showSelectBranchDialog(true)
    }

    fun onBranchClick(branch: Branch) {
        sourceArgs.currentBranch = branch.name
        showBranchName()

        sourceArgs.folders.clear()
        showFolderName()

        currentCommitHash = null
        hardRefreshFiles()

        hideBranchDialog()
    }

    fun hideBranchDialog() {
        viewState.hideBranchDialog()
    }

    private suspend fun getFiles(page: String?): PagedResponse<Source> {
        val commitHash = currentCommitHash

        return if (commitHash == null) {
            val response = repoRepository.getCommitsOfBranch(
                sourceArgs.slugs.workspace,
                sourceArgs.slugs.repository,
                sourceArgs.currentBranch
            ).apply {
                currentCommitHash = values.first().hash

            }
            getFilesPage(response.values.first().hash, page)
        } else {
            getFilesPage(commitHash, page)
        }
    }

    private suspend fun getFilesPage(commitHash: String, page: String?) =
        sourceRepository.getFiles(
            sourceArgs.slugs.workspace,
            sourceArgs.slugs.repository,
            commitHash,
            sourceArgs.folders.joinToString("/"),
            page
        )

    private fun showSelectBranchDialog(
        isTags: Boolean
    ) {
        viewState.showBranchDialogProgress(true)
        launchOnDefault(
            CoroutineExceptionHandler { _, throwable ->
                launchOnMain {
                    viewState.showBranchDialogProgress(false)
                    viewState.showErrorMessage(throwable)
                }
            }
        ) {
            val branches = getBranches(isTags)
            switchToUi {
                viewState.showBranchDialogProgress(false)
                viewState.showBranchDialog(branches, isTags)
            }
        }
    }

    private suspend fun getBranches(
        isTags: Boolean,
    ) = getBranchesPage(isTags).values

    private suspend fun getBranchesPage(
        isTags: Boolean,
        page: String? = null
    ): PagedResponse<Branch> {
        var response = fetchResponse(isTags, page)
        var nextPage = response.nextPage

        while (nextPage != null) {
            val tmp = fetchResponse(isTags, nextPage)
            response = PagedResponse(
                page = tmp.page,
                size = tmp.size + response.size,
                pagelen = tmp.pagelen,
                values = response.values.toMutableList().apply {
                    addAll(tmp.values)
                },
                next = tmp.next
            )
            nextPage = tmp.nextPage
        }
        return response
    }

    private suspend fun fetchResponse(isTags: Boolean, page: String? = null) =
        if(isTags)
            branchRepository.getTags(
                sourceArgs.slugs.workspace,
                sourceArgs.slugs.repository,
                page)
        else
            branchRepository.getBranches(
                sourceArgs.slugs.workspace,
                sourceArgs.slugs.repository,
                page)

    private fun showFolderName() {
        if (sourceArgs.folders.isNotEmpty()) {
            viewState.showFolderName(sourceArgs.folders.last())
        } else {
            viewState.showFolderName()
        }
    }

    private fun showBranchName() {
        viewState.showBranchName(sourceArgs.currentBranch)
    }

    private fun hardRefreshFiles() {
        paginator.reset()
        paginator.refresh()
    }
}
