/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details

import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName.REPOSITORY_DETAILS
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.args.CommitsArgs
import com.akvelon.bitbuckler.model.entity.args.SourceArgs
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.model.repository.RepoRepository
import com.akvelon.bitbuckler.model.repository.SourceRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.domain.repo.CachedReposUseCase
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.screen.Screen
import com.akvelon.bitbuckler.ui.state.ScreenData
import com.akvelon.bitbuckler.ui.state.ScreenManagerRequestState
import com.akvelon.bitbuckler.ui.state.ScreenStateManager
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@InjectViewState
class RepositoryDetailsPresenter(
    router: Router,
    private val slugs: Slugs,
    private val analytics: AnalyticsProvider,
    private val repoRepository: RepoRepository,
    private val cachedReposUseCase: CachedReposUseCase,
    private val sourceRepository: SourceRepository
) : BasePresenter<RepositoryDetailsView>(router) {

    private var descriptionIsCollapsed = true

    private val stateManager = ScreenStateManager(
        {
            launchOnDefault(
                CoroutineExceptionHandler { _, throwable ->
                    launchOnMain { screenStateCallback.failure(throwable) }
                }
            ) {
                val response = getRepository()
                switchToUi { screenStateCallback.success(response) }
            }
        },
        object : ScreenStateManager.ViewController<Repository> {
            override fun showEmptyProgress(show: Boolean) {
                viewState.showEmptyProgress(show)
            }

            override fun showEmptyError(show: Boolean, error: Throwable?) {
                if (error is HttpException && error.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                    viewState.setNoAccessError()
                } else {
                    when (error) {
                        is SocketTimeoutException, is UnknownHostException ->
                            viewState.setNoInternetConnectionError()

                        is Throwable -> {
                            viewState.setUnknownError()
                            NonFatalError.log(error, screenName)
                        }
                    }
                }
                viewState.showErrorView(show)
            }

            override fun showEmptyView(show: Boolean) {
            }

            override fun showData(data: Repository) {
                viewState.showData(data)
            }

            override fun showErrorMessage(error: Throwable) {
                viewState.showError(error)
            }

            override fun showRefreshProgress(show: Boolean) {
                viewState.showRefreshingProgress(show)
            }
        }
    )

    private val screenStateCallback: ScreenManagerRequestState<Repository> = stateManager

    override fun onFirstViewAttach() {
        analytics.report(AnalyticsEvent.Repository.RepositoryScreen)

        refreshRepository()
    }

    fun refreshRepository() {
        stateManager.refresh()
        getReadme()
    }

    fun toggleDescription() {
        descriptionIsCollapsed = !descriptionIsCollapsed
        viewState.toggleDescription(descriptionIsCollapsed)
    }

    fun onSourceClick(repository: Repository) {
        repository.mainBranch?.let {
            router.navigateTo(
                Screen.sourceList(
                    SourceArgs(
                        slugs,
                        it.name
                    )
                )
            )
        }
    }

    fun onCommitsClick(repository: Repository) {
        repository.mainBranch?.let {
            router.navigateTo(
                Screen.commitList(
                    CommitsArgs(
                        slugs,
                        it.name
                    )
                )
            )
        }
    }

    fun onBranchesClick() {
        router.navigateTo(Screen.branchList(slugs))
    }

    fun onPullRequestClick() {
        router.navigateTo(Screen.pullRequestList(slugs))
    }

    fun onIssuesClick() {
        router.navigateTo(Screen.issueList(slugs))
    }

    private suspend fun getRepository() : ScreenData<Repository> {
        val repository = repoRepository.getRepositoryDetails(
            slugs.workspace,
            slugs.repository
        )
        return ScreenData(repository)
    }

    private fun getReadme() = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain { viewState.hideReadme() }
        }
    ) {
        val repository = repoRepository.getRepositoryDetails(
            slugs.workspace,
            slugs.repository
        )
        if (repository.mainBranch != null) {
            val response = repoRepository.getCommitsOfBranch(
                slugs.workspace,
                slugs.repository,
                repository.mainBranch.name
            )
            val fileContent = sourceRepository.getFileContent(
                slugs.workspace,
                slugs.repository,
                response.values.first().hash,
                README_FILE_NAME)
            asyncOnDefault {
                val string = fileContent.string()
                switchToUi { viewState.showReadme(string) }
            }
        } else { throw IllegalArgumentException() }
    }

    fun save(recentRepository: RecentRepository) = launchOnDefault {
        repoRepository.apply {
            saveToRecent(recentRepository)
            shrinkRecent()
        }
    }

    fun trackRepository() = launchOnDefault {
        val repository = getRepository()
        if(cachedReposUseCase.tryTrackRepository(repository.value.uuid)) {
            switchToUi { viewState.setRepoTracked(true) }
        } else {
            switchToUi {
                viewState.showLimitTrackedRepoError()
                viewState.setRepoTracked(false)
            }
        }

        analytics.report(AnalyticsEvent.Repository.RepositoryTrackSetup)

    }

    fun unTrackRepository() = launchOnDefault {
        val repository = getRepository()
        cachedReposUseCase.unTrackRepository(repository.value.uuid)
        analytics.report(AnalyticsEvent.Repository.RepositoryTrackSetup)
    }

    companion object {
        const val README_FILE_NAME = "README.md"

        private const val screenName = REPOSITORY_DETAILS
    }
}

