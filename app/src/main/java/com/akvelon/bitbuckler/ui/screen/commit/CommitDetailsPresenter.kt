/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 24 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.commit

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.args.ChangesArgs
import com.akvelon.bitbuckler.model.entity.args.CommitDetailsArgs
import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.model.repository.CommitDetailsRepository
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.screen.Screen
import com.akvelon.bitbuckler.ui.state.ScreenData
import com.akvelon.bitbuckler.ui.state.ScreenManagerRequestState
import com.akvelon.bitbuckler.ui.state.ScreenStateManager
import com.akvelon.bitbuckler.ui.state.screen.CommitDetails
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
class CommitDetailsPresenter(
    router: Router,
    val commitDetailsArgs: CommitDetailsArgs,
    private val accountRepository: AccountRepository,
    private val commitDetailsRepository: CommitDetailsRepository,
) : BasePresenter<CommitDetailsView>(router) {

    private val stateManager = ScreenStateManager(
        {
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { stateCallback.failure(throwable) }
                }
            ) {
                val commitDetails = getCommitDetails()
                switchToUi { stateCallback.success(commitDetails) }
            }
        },
        object : ScreenStateManager.ViewController<CommitDetails> {

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

            override fun showData(data: CommitDetails) {
                viewState.showData(data)
            }

            override fun showEmptyView(show: Boolean) {
            }

            override fun showErrorMessage(error: Throwable) {
                viewState.showError(error)
            }

            override fun showRefreshProgress(show: Boolean) {
                viewState.showRefreshProgress(show)
            }
        }
    )

    private val stateCallback: ScreenManagerRequestState<CommitDetails> = stateManager

    override fun onFirstViewAttach() {
        viewState.apply {
            hideBottomNav()
            showComments()
        }
        stateManager.refresh()
    }

    fun onFilesClick(commitDetailsHash: String) {
        commitDetailsArgs.title = commitDetailsHash.take(COMMIT_HASH_LENGTH)

        router.navigateTo(Screen.changes(ChangesArgs.fromCommitDetailsArgs(commitDetailsArgs)))
    }

    fun refreshCommitDetails() = stateManager.refresh()

    override fun onDestroy() {
        super.onDestroy()
        stateManager.release()
    }

    private suspend fun getCommitDetails() = with(commitDetailsArgs) {
        val commit = commitDetailsRepository.getCommitDetails(workspaceSlug, repositorySlug, hash)
        val account = accountRepository.getCurrentAccount()
        val builds = commitDetailsRepository.getCommitStatuses(workspaceSlug, repositorySlug, hash)
        ScreenData(CommitDetails(commit, account, builds.values))
    }

    companion object {
        private const val screenName = ScreenName.COMMIT_DETAILS
        private const val COMMIT_HASH_LENGTH = 7
    }
}
