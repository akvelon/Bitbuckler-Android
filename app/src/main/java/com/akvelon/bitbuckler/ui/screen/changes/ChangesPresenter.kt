/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 January 2021
 */

package com.akvelon.bitbuckler.ui.screen.changes

import com.akvelon.bitbuckler.extension.createCommentsTreeList
import com.akvelon.bitbuckler.extension.dropFirstSymbol
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.args.*
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.*
import com.akvelon.bitbuckler.model.repository.ChangesRepository
import com.akvelon.bitbuckler.model.repository.CommentRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.CHANGES
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.helper.MentionHelper
import com.akvelon.bitbuckler.ui.screen.Screen
import com.akvelon.bitbuckler.ui.screen.pullrequest.comment.inline.InlineCommentsFragment
import com.akvelon.bitbuckler.ui.state.ScreenData
import com.akvelon.bitbuckler.ui.state.ScreenManagerRequestState
import com.akvelon.bitbuckler.ui.state.ScreenStateManager
import com.akvelon.bitbuckler.ui.state.screen.DiffDetails
import com.github.terrakok.cicerone.ResultListenerHandler
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
class ChangesPresenter(
    router: Router,
    private val changesArgs: ChangesArgs,
    private val analytics: AnalyticsProvider,
    private val changesRepository: ChangesRepository,
    private val commentRepository: CommentRepository,
    private val mentionHelper: MentionHelper
) : BasePresenter<ChangesView>(router) {

    private var resultHandler: ResultListenerHandler? = null

    private val stateManager = ScreenStateManager(
        {
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { stateCallback.failure(throwable) }
                }
            ) {
                val diffs = getDiffs()
                switchToUi { stateCallback.success(diffs) }
            }
        },
        object : ScreenStateManager.ViewController<DiffDetails> {

            override fun showEmptyProgress(show: Boolean) {
                viewState.showEmptyProgress(show)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                if (error is HttpException && error.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    viewState.showEmptyView(show)
                } else {
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
            }

            override fun showData(data: DiffDetails) {
                viewState.showData(data)
            }

            override fun showEmptyView(show: Boolean) {
                viewState.showEmptyView(show)
            }

            override fun showErrorMessage(error: Throwable) {
                viewState.showError(error)
            }

            override fun showRefreshProgress(show: Boolean) {
                viewState.showRefreshProgress(show)
            }
        }
    )

    private val stateCallback: ScreenManagerRequestState<DiffDetails> = stateManager

    override fun onFirstViewAttach() {
        viewState.hideBottomNav()
        changesArgs.title?.let { viewState.showTitle(it) }

        when (changesArgs.scope) {
            ChangesScope.PULL_REQUEST_CHANGES -> analytics.report(AnalyticsEvent.PullRequest.PullRequestFilesScreen)
            ChangesScope.COMMIT_CHANGES -> analytics.report(AnalyticsEvent.CommitScreen.CommitFilesScreen)
        }

        refreshDiffs()
    }

    fun onCommentClick(file: FileDiff, line: LineDiff?, filePosition: Int, linePosition: Int?) {
        resultHandler = router.setResultListener(InlineCommentsFragment.INLINE_KEY_RESULT) {
            viewState.notifyLineOrFileChanged(filePosition, linePosition)
            logLineOrFileChanged(linePosition)
        }

        router.navigateTo(
            Screen.inlineComments(
                InlineCommentsArgs(
                    CommentsArgs.fromChangesArgs(changesArgs),
                    file,
                    line
                )
            )
        )
    }

    private fun logLineOrFileChanged(linePosition: Int?) {
        if (linePosition == null)
            analytics.report(AnalyticsEvent.FilesScreen.FilesFileCommentSent)
        else
            analytics.report(AnalyticsEvent.FilesScreen.FilesInlineCommentSent)
        analytics.report(AnalyticsEvent.FilesScreen.FilesCommentSent)
    }

    fun refreshDiffs() = stateManager.refresh()

    fun onFileClick() {
         analytics.report(AnalyticsEvent.FilesListNavigation.FilesListNavigationSuccess)
    }

    override fun onDestroy() {
        super.onDestroy()

        stateManager.release()
        resultHandler?.dispose()
    }

    private suspend fun getDiffs(): ScreenData<DiffDetails> {
        val changes = getChanges()
        val files = getFiles()
        val comments = getInlineComments()

        files.values.forEach { file ->
            changes.find { change -> change.filePath.dropFirstSymbol() == file.filePath }?.let {
                it.status = file.status
            }
        }

        comments.forEach { comment ->
            val file = changes.find { change ->
                change.filePath.dropFirstSymbol() == comment.element.inline?.path
            }

            file?.let {
                val line = it.lines.find { line ->
                    when (line) {
                        is RemovedLine -> line.oldNumber == comment.element.inline?.from
                        is UnchangedLine ->
                            line.newNumber == comment.element.inline?.to ||
                                    line.oldNumber == comment.element.inline?.from
                        is AddedLine -> line.newNumber == comment.element.inline?.to
                        is ConflictedLine -> line.newNumber == comment.element.inline?.to
                        else -> false
                    }
                }

                if (line == null) {
                    if (comment.element.isFileComment) {
                        it.comments.add(comment)
                    } else {
                        it.outdatedComments += comment.count
                    }
                } else {
                    line.comments.add(comment)
                }
            }
        }

        return ScreenData(DiffDetails(changes, files.values))
    }

    private suspend fun getChanges() = changesRepository.getChanges(changesArgs)

    private suspend fun getFiles() = changesRepository.getFiles(changesArgs)

    private suspend fun getInlineComments(): List<TreeNode<Comment>> {
        val commentsList = getInlineCommentsPage().values
        return commentsList.map { comment ->
            mentionHelper.setMentions(comment.content.raw).let {
                if (comment.content.raw != it) {
                    comment.updateComment(
                        comment.content.setMentionedRaw(it)
                    )
                } else {
                    comment
                }
            }
        }.sortedBy { it.createdOn }
            .toMutableList()
            .createCommentsTreeList()
    }

    private suspend fun getInlineCommentsPage(page: String? = null): PagedResponse<Comment> {
        val scope: CommentScope = when (changesArgs.scope) {
            ChangesScope.COMMIT_CHANGES -> CommentScope.COMMITS
            ChangesScope.PULL_REQUEST_CHANGES -> CommentScope.PULL_REQUESTS
        }
        return with(changesArgs) {
            var response = commentRepository.getInlineComments(
                this.workspaceSlug,
                this.repositorySlug,
                scope,
                id,
                page
            )
            var nextPage = response.nextPage
            while (nextPage != null) {
                val tmp = commentRepository.getInlineComments(
                    this.workspaceSlug,
                    this.repositorySlug,
                    scope,
                    id,
                    nextPage
                )
                response = PagedResponse(
                    page = tmp.page,
                    size = tmp.size,
                    pagelen = tmp.pagelen,
                    values = response.values.toMutableList().apply {
                        addAll(tmp.values)
                    },
                    next = tmp.next
                )
                nextPage = tmp.nextPage
            }
            response
        }
    }

    fun onFilesButtonClicked() {
        analytics.report(AnalyticsEvent.FilesScreen.FilesListNavigationScreen)
    }

    companion object {
        private const val screenName = CHANGES
    }
}

