package com.akvelon.bitbuckler.model.repository.analytics

sealed class AnalyticsEvent(open val eventName: String, open val parameters: Map<String, Any>) {

    sealed class Login(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object LoginScreen : Login(eventName = LOGIN_SCREEN)
        object LoginButtonClicked : Login(eventName = LOGIN_BUTTON_CLICKED)
        object FirstOpen : Login(eventName = FIRST_OPEN)
        object LoginSuccessfully : Login(eventName = LOGIN_SUCCESSFULLY)
        object LoginFailed : Login(eventName = LOGIN_FAILED)
        object AuthorizedUserEntered : Login(eventName = AUTHORIZED_USER_ENTERED)
        object RefreshTokenExpired : Login(eventName = REFRESH_TOKEN_EXPIRED)
    }

    sealed class PrivacyPolicy(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object PrivacyPolicyScreen : PrivacyPolicy(eventName = PRIVACY_POLICY_SCREEN)
        object PrivacyPolicyAccepted : PrivacyPolicy(eventName = PRIVACY_POLICY_ACCEPTED)
    }

    sealed class Activity(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object ActivityScreen : Activity(eventName = ACTIVITY_SCREEN)
        object ActivityFilterOpen : Activity(eventName = ACTIVITY_FILTER_OPEN)
        object ActivityFilterMerged : Activity(eventName = ACTIVITY_FILTER_MERGED)
        object ActivityFilterDeclined : Activity(eventName = ACTIVITY_FILTER_DECLINED)
        object ActivityTrackReposOpen : Activity(eventName = ACTIVITY_TRACK_REPOS_OPEN)
        object ActivityPullToRefresh : Activity(eventName = ACTIVITY_PULL_TO_REFRESH)
        class ActivityScreenLoading(loadingSecs: Double) : Activity(eventName = ACTIVITY_SCREEN_LOADING,
            parameters = mapOf(ACTIVITY_SCREEN_LOADING_PARAMETER_KEY to loadingSecs))
    }

    sealed class PRLink(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object PRLinkScreen : PRLink(eventName = PR_LINK_SCREEN)
        object PRLinkCancel : PRLink(eventName = PR_LINK_CANCEL)
        object PRLinkSubmit : PRLink(eventName = PR_LINK_SUBMIT)
        object PRLinkFailed : PRLink(eventName = PR_LINK_FAILED)
    }

    sealed class PullRequest(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object PullRequestScreen : PullRequest(eventName = PULL_REQUEST_SCREEN)
        object PullRequestMergeSuccess : PullRequest(eventName = PULL_REQUEST_MERGE_SUCCESS)
        object PullRequestFailed : PullRequest(eventName = PULL_REQUEST_MERGE_FAIL)
        object PullRequestApprove : PullRequest(eventName = PULL_REQUEST_APPROVE)
        object PullRequestUnApprove : PullRequest(eventName = PULL_REQUEST_UNAPPROVE)
        object PullRequestDecline : PullRequest(eventName = PULL_REQUEST_DECLINE)
        object PullRequestCommentSent : PullRequest(eventName = PULL_REQUEST_COMMENT_SENT)
        object PullRequestFilesScreen : PullRequest(eventName = PULL_REQUEST_FILES_SCREEN)
        object PullRequestCommitsScreen : PullRequest(eventName = PULL_REQUEST_COMMITS_SCREEN)
    }

    sealed class Profile(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object ProfileScreen : Profile(eventName = PROFILE_SCREEN)
        object ProfileRate : Profile(eventName = PROFILE_RATE)
        object Logout : Profile(eventName = LOGOUT)
        object ProfileAkvelonInc : Profile(eventName = PROFILE_AKVELON_INC)
    }

    sealed class SwitchAccount(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object SwitchAccountScreen : SwitchAccount(eventName = SWITCH_ACCOUNT_SCREEN)
        object SwitchAccountSuccess : SwitchAccount(eventName = SWITCH_ACCOUNT_SUCCESS)
        object SwitchAccountRemoveAccount : SwitchAccount(eventName = SWITCH_ACCOUNT_REMOVE_ACCOUNT)
        object SwitchAccountAddAccountSuccess : SwitchAccount(eventName = SWITCH_ACCOUNT_ADD_ACCOUNT_SUCCESS)
        object SwitchAccountCancel : SwitchAccount(eventName = SWITCH_ACCOUNT_CANCEL)
    }

    sealed class CommitScreen(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object CommitCommentSent : CommitScreen(eventName = COMMIT_COMMENT_SENT)
        object CommitFilesScreen : CommitScreen(eventName = COMMIT_FILES_SCREEN)
    }

    sealed class FilesScreen(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object FilesInlineCommentSent : FilesScreen(eventName = FILES_INLINE_COMMENT_SENT)
        object FilesFileCommentSent : FilesScreen(eventName = FILES_FILE_COMMENT_SENT)
        object FilesCommentSent : FilesScreen(eventName = FILES_COMMENT_SENT)
        object FilesListNavigationScreen : FilesScreen(eventName = FILES_LIST_NAVIGATION_SCREEN)
    }

    sealed class FilesListNavigation(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object FilesListNavigationSuccess : FilesListNavigation(eventName = FILES_LIST_NAVIGATION_SUCCESS)
    }

    sealed class RepositoriesTab(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object RepositoriesTabScreen : RepositoriesTab(eventName = REPOSITORIES_TAB_SCREEN)
        object RepositoriesTabRecentClicked : RepositoriesTab(eventName = REPOSITORIES_TAB_RECENT_CLICKED)
        object RepositoriesTabRemoveRecent : RepositoriesTab(eventName = REPOSITORIES_TAB_REMOVE_RECENT)
    }

    sealed class RepositoriesList(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object RepositoriesListScreen : RepositoriesList(eventName = REPOSITORIES_LIST_SCREEN)
        object RepositoriesListSearch : RepositoriesList(eventName = REPOSITORIES_LIST_SEARCH)
        object RepositoriesListTrackSetup : RepositoriesList(eventName = REPOSITORIES_LIST_TRACK_SETUP)
    }

    sealed class Repository(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object RepositoryScreen : Repository(eventName = REPOSITORY_SCREEN)
        object RepositorySourceScreen : Repository(eventName = REPOSITORY_SOURCE_SCREEN)
        object RepositoryCommitsScreen : Repository(eventName = REPOSITORY_COMMITS_SCREEN)
        object RepositoryBranchesScreen : Repository(eventName = REPOSITORY_BRANCHES_SCREEN)
        object RepositoryPullRequestScreen : Repository(eventName = REPOSITORY_PULL_REQUESTS_SCREEN)
        object RepositoryTrackSetup : Repository(eventName = REPOSITORY_TRACK_SETUP)
    }

    sealed class PullRequestsScreen(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object PullRequestFilterOpen : PullRequestsScreen(eventName = PULL_REQUESTS_FILTER_OPEN)
        object PullRequestFilterMerged : PullRequestsScreen(eventName = PULL_REQUESTS_FILTER_MERGED)
        object PullRequestFilterDeclined : PullRequestsScreen(eventName = PULL_REQUESTS_FILTER_DECLINED)
    }

    sealed class ChooseRepositoriesPopUp(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object ChooseReposPopUpScreen : ChooseRepositoriesPopUp(eventName = CHOOSE_REPOS_POP_UP)
        object ChooseReposChoose : ChooseRepositoriesPopUp(eventName = CHOOSE_REPOS_CHOOSE)
        object ChooseReposCancelRepeat : ChooseRepositoriesPopUp(eventName = CHOOSE_REPOS_CANCEL_REPEAT)
        object ChooseReposCancelNoRepeat : ChooseRepositoriesPopUp(eventName = CHOOSE_REPOS_CANCEL_NO_REPEAT)
    }

    sealed class TrackingReposSetup(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object TrackReposSetupScreen : TrackingReposSetup(eventName = TRACK_REPOS_SETUP_SCREEN)
        object TrackReposSetupTrackConfirm : TrackingReposSetup(eventName = TRACK_REPOS_SETUP_TRACK_CONFIRM)
        object TrackReposSetupNoMoreAlert : TrackingReposSetup(eventName = TRACK_REPOS_SETUP_NO_MORE_ALERT)
    }

    sealed class BusinessLogicEvents(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {
        object DeepLinkPR : BusinessLogicEvents(eventName = DEEP_LINK_PR)
    }

    sealed class DonationEvents(override val eventName: String, override val parameters: Map<String, Any> = emptyMap()) :
        AnalyticsEvent(eventName, parameters) {

        object DonationScreen : BusinessLogicEvents(eventName = DONATION_SCREEN)

        object CloseDonationScreen : BusinessLogicEvents(eventName = CLOSE_DONATION_SCREEN)

        object CloseDonationScreenNotRemind : BusinessLogicEvents(eventName = CLOSE_DONATION_SCREEN_NOT_REMIND)

        object DonationScreenComplimentClicked : BusinessLogicEvents(eventName = DONATION_SCREEN_COMPLIMENT_CLICKED)

        object DonationScreenComplimentFailed : BusinessLogicEvents(eventName = DONATION_SCREEN_COMPLIMENT_FAILED)

        object DonationScreenComplimentSuccess : BusinessLogicEvents(eventName = DONATION_SCREEN_COMPLIMENT_SUCCESS)

        object DonationScreenComplimentCancelled : BusinessLogicEvents(eventName = DONATION_SCREEN_COMPLIMENT_CANCELED)

        object DonationScreenRestored : BusinessLogicEvents(eventName = DONATION_SCREEN_RESTORED)
    }
}