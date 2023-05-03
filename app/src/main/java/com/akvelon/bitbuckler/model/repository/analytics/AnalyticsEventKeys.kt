package com.akvelon.bitbuckler.model.repository.analytics

/**
 * logic screen
 */

//When login screen appears
const val LOGIN_SCREEN = "login_screen"
//When user click on “Sign in” button on the bottom
const val LOGIN_BUTTON_CLICKED = "login_button_clicked"
//When user just installed the app
const val FIRST_OPEN = "first_open"
//If user successfully logged in
const val LOGIN_SUCCESSFULLY = "login_successfully"
//If user failed to login
const val LOGIN_FAILED = "login_failed"
//If user already authorized
const val AUTHORIZED_USER_ENTERED = "authorized_user_entered"
//If user’s refresh token was expired/invalid. This can be happen in update access token request
const val REFRESH_TOKEN_EXPIRED = "refresh_token_expired"

/**
 * privacy policy screen
 */
//When privacy screen appears
const val PRIVACY_POLICY_SCREEN = "privacy_policy_screen"
//When user accept privacy policy
const val PRIVACY_POLICY_ACCEPTED = "privacy_policy_accepted"

/**
 * activity screen
 */
//When activity screen appears
const val ACTIVITY_SCREEN = "activity_screen"
//when user filter by open (clicking on "open" button in filter)
const val ACTIVITY_FILTER_OPEN = "activity_filter_open"
//When user filter by “Merged”
const val ACTIVITY_FILTER_MERGED = "activity_filter_merged"
//When user filter by “Declined“
const val ACTIVITY_FILTER_DECLINED = "activity_filter_declined"
//When user click on “Track repos”
const val ACTIVITY_TRACK_REPOS_OPEN = "activity_track_repos_open"
//If user pull to refresh
const val ACTIVITY_PULL_TO_REFRESH = "activity_pull_to_refresh"
//Calculate every time when refresh pull requests list
const val ACTIVITY_SCREEN_LOADING = "activity_screen_loading"
//(Optional) Calculate how much time this screen loading.
const val ACTIVITY_SCREEN_LOADING_PARAMETER_KEY = "sec"

/**
 * PRLink screen
 */
//When pr link screen appears
const val PR_LINK_SCREEN = "pr_link_screen"
//When user click on cancel button
const val PR_LINK_CANCEL = "pr_link_cancel"
//When user click on submit button
const val PR_LINK_SUBMIT = "pr_link_submit"
//If user has an error when he click submit
const val PR_LINK_FAILED = "pr_link_failed"

/**
 * pull request screen
 */
//When pull request screen appears
const val PULL_REQUEST_SCREEN = "pull_request_screen"
//If user successfully merge PR
const val PULL_REQUEST_MERGE_SUCCESS = "pull_request_merge_success"
//If user fail to merge PR
const val PULL_REQUEST_MERGE_FAIL = "pull_request_merge_fail"
//If user approve PR
const val PULL_REQUEST_APPROVE = "pull_request_approve"
//If user unapprove PR
const val PULL_REQUEST_UNAPPROVE = "pull_request_unapprove"
//if user successfully decline PR
const val PULL_REQUEST_DECLINE = "pull_request_decline"
//When user send a comment (not matter reply/send)
const val PULL_REQUEST_COMMENT_SENT = "pull_request_comment_sent"
//When user click on Files button
const val PULL_REQUEST_FILES_SCREEN = "pull_request_files_screen"
//When user click on Commits button
const val PULL_REQUEST_COMMITS_SCREEN = "pull_request_commits_screen"

/**
 * profile screen
 */
//When user open profile screen
const val PROFILE_SCREEN = "profile_screen"
//When user click on rate application
const val PROFILE_RATE = "profile_rate"
//When user succesfully logout
const val LOGOUT = "logout"
//When user click on Akvelon Inc. link in the bottom
const val PROFILE_AKVELON_INC = "profile_akvelon_inc"

/**
 * switch account screen
 */
//When swtich account screen appears
const val SWITCH_ACCOUNT_SCREEN = "switch_account_screen"
//When user successfully switch account clicking on account
const val SWITCH_ACCOUNT_SUCCESS = "switch_account_success"
//When user delete account from the list
const val SWITCH_ACCOUNT_REMOVE_ACCOUNT = "switch_account_remove_account"
//When user successfully add another account
const val SWITCH_ACCOUNT_ADD_ACCOUNT_SUCCESS = "switch_account_add_account_success"
//If user click cancel button
const val SWITCH_ACCOUNT_CANCEL = "switch_account_cancel"

/**
 * commit screen
 */
//When user successfully send comment
const val COMMIT_COMMENT_SENT = "commit_comment_sent"
//When user click on FILES button
const val COMMIT_FILES_SCREEN = "commit_files_screen"

/**
 * files screen
 */
//If user successfully sent inline comment
const val FILES_INLINE_COMMENT_SENT = "files_inline_comment_sent"
//If user successfully sent file comment (second screen)
const val FILES_FILE_COMMENT_SENT = "files_file_comment_sent"
//If user successfully sent any type of comment in files
const val FILES_COMMENT_SENT = "files_comment_sent"
//If user click on files list button (blue button in the right bottom)
const val FILES_LIST_NAVIGATION_SCREEN = "files_list_navigation_screen"

/**
 * files list navigation screen
 */
//When user click on any file in the list
const val FILES_LIST_NAVIGATION_SUCCESS = "files_list_navigation_success"

/**
 * repositories tab screen
 */
//When repositories screen appears
const val REPOSITORIES_TAB_SCREEN = "repositories_tab_screen"
//If user click on recent repo
const val REPOSITORIES_TAB_RECENT_CLICKED = "repositories_tab_recent_clicked"
//If user remove repo from recent(x red button)
const val REPOSITORIES_TAB_REMOVE_RECENT = "repositories_tab_remove_recent"

/**
 * repositories list screen
 */
//When repositories list screen appear
const val REPOSITORIES_LIST_SCREEN = "repositories_list_screen"
//When user use any request in search field (make search)
const val REPOSITORIES_LIST_SEARCH = "repositories_list_search"
//When user click on track/untrack
const val REPOSITORIES_LIST_TRACK_SETUP = "repositories_list_track_setup"

/**
 * repository screen
 */
//When repository screen appears
const val REPOSITORY_SCREEN = "repository_screen"
//When user clicks on source
const val REPOSITORY_SOURCE_SCREEN = "repository_source_screen"
//When user clicks on commits
const val REPOSITORY_COMMITS_SCREEN = "repository_commits_screen"
//When user clicks on branches
const val REPOSITORY_BRANCHES_SCREEN = "repository_branches_screen"
//When user clicks on Pull requests
const val REPOSITORY_PULL_REQUESTS_SCREEN = "repository_pull_requests_screen"
//When user clicks on TRACK/UNTRACK
const val REPOSITORY_TRACK_SETUP = "repository_track_setup"

/**
 * pull request screen
 */
//When user filters by open (clicking on “Open“ button in filter)
const val PULL_REQUESTS_FILTER_OPEN = "pull_requests_filter_open"
//When user filters by “Merged”
const val PULL_REQUESTS_FILTER_MERGED = "pull_requests_filter_merged"
//When user filters by “Declined“
const val PULL_REQUESTS_FILTER_DECLINED = "pull_requests_filter_declined"

/**
 * choose repositories pop up screen
 */
//When choose repositories pop up screen appears
const val CHOOSE_REPOS_POP_UP = "choose_repos_pop_up"
//When user clicks CHOOSE button
const val CHOOSE_REPOS_CHOOSE = "choose_repos_choose"
//When user cancel without don’t show this again
const val CHOOSE_REPOS_CANCEL_REPEAT = "choose_repos_cancel_repeat"
//When user cancels without don’t show this again
const val CHOOSE_REPOS_CANCEL_NO_REPEAT = "choose_repos_cancel_no_repeat"

/**
 * tracking repos set up screen
 */
//When tracing repos set up screen appears
const val TRACK_REPOS_SETUP_SCREEN = "track_repos_setup_screen"
//When user click on “Track selected repositories“
const val TRACK_REPOS_SETUP_TRACK_CONFIRM = "track_repos_setup_track_confirm"
//When pop up with alert “No more 10(5) repos” (second screen)
const val TRACK_REPOS_SETUP_NO_MORE_ALERT = "track_repos_setup_no_more_alert"

/**
 * business logic events
 */
//When user navigate to PR using deep linking
const val DEEP_LINK_PR = "deep_link_pr"


/**
 * donation events screen
 */
//When screen donation screen appear
const val DONATION_SCREEN = "donation_screen"
//When user close the window without donation
const val CLOSE_DONATION_SCREEN = "close_donation_screen"
//When user close window with unchecked remind me later
const val CLOSE_DONATION_SCREEN_NOT_REMIND = "close_donation_screen_not_remind"
//When user click on compliment
const val DONATION_SCREEN_COMPLIMENT_CLICKED = "donation_screen_compliment_clicked"
//When compliment failed send
const val DONATION_SCREEN_COMPLIMENT_FAILED = "donation_screen_compliment_failed"
//When user successfully donate
const val DONATION_SCREEN_COMPLIMENT_SUCCESS = "donation_screen_compliment_success"
//When user cancel compliment
const val DONATION_SCREEN_COMPLIMENT_CANCELED = "donation_screen_compliment_canceled"
//When user restored his donation
const val DONATION_SCREEN_RESTORED = "donation_screen_restored"