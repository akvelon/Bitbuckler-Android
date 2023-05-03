package com.akvelon.bitbuckler.ui.screen.activity.dialog

import com.akvelon.bitbuckler.domain.prlink.PRLinkParser
import com.akvelon.bitbuckler.entity.pullrequest.PullRequestParseEntity
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.ui.base.BasePresenter
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.InjectViewState

@InjectViewState
class PrLinkPresenter(
    router: Router,
    private val prLinkParser: PRLinkParser,
    private val analytics: AnalyticsProvider
) : BasePresenter<PrParseView>(router) {

    fun onPRLinkViewCreated(){
        analytics.report(AnalyticsEvent.PRLink.PRLinkScreen)
    }

    fun onPullRequestLink(urlLink: String, isDeepLink: Boolean) = launchOnDefault(
        CoroutineExceptionHandler { _, throwable ->
            launchOnMain { viewState.onPrParseError(throwable.message ?: "Unknown error") }
        }
    ) {
        val entitiy = prLinkParser.parseLink(urlLink)
        when (entitiy) {
            is PullRequestParseEntity.PRParseSuccess -> {
                switchToUi {
                    viewState.onPrParseSuccess()
                    router.navigateTo(
                        Screen.pullRequestDetails(
                            PullRequestArgs(
                                entitiy.args.workspaceSlug,
                                entitiy.args.repositorySlug,
                                entitiy.args.id
                            )
                        )
                    )
                    if (isDeepLink)
                        analytics.report(AnalyticsEvent.BusinessLogicEvents.DeepLinkPR)
                }
            }
            is PullRequestParseEntity.PRParseFailed -> {
                if (!isDeepLink)
                    analytics.report(AnalyticsEvent.PRLink.PRLinkFailed)
                switchToUi { viewState.onPrParseError(entitiy.message) }
            }
        }
    }

    fun logCancelEvent() {
        analytics.report(AnalyticsEvent.PRLink.PRLinkCancel)
    }

    fun logConfirmEvent() {
        analytics.report(AnalyticsEvent.PRLink.PRLinkSubmit)
    }
}