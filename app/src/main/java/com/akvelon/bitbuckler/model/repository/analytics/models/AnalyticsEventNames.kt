/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 10 March 2021
 */

package com.akvelon.bitbuckler.model.repository.analytics.models

object AnalyticsEventNames {

    object CommitDetails {
        const val OPENED = "commit_details_opened"
    }

    object PrDetails {
        const val BRANCHES_DIALOG_OPENED = "pr_dialog_branches_opened"
        const val BUILDS_DIALOG_OPENED = "pr_dialog_builds_opened"
        const val OPENED = "pr_opened"
    }

    object Recent {
        const val REPOSITORY_CLICKED = "recent_repository_clicked"
        const val REPOSITORY_REMOVED = "recent_repository_removed"
    }

    object RepoDetails {
        const val ISSUES_FILTER_CHANGED = "issues_filter_changed"
        const val PR_FILTER_CHANGED = "repo_details_pr_filter_changed"
    }

    object Diff {
        const val BOTTOM_SHEET_FILE_CLICKED = "diff_bottom_sheet_file_clicked"
    }

    object User {
        const val AUTHORIZED_ENTERED = "authorized_user_entered"
        const val UNAUTHORIZED_ENTERED = "unauthorized_user_entered"
    }

    object Search {
        const val SEARCH_NOT_ENABLED = "search_not_enabled"
        const val ENABLE_SEARCH_CLICKED = "enable_search_clicked"
        const val RESULT_CLICKED = "search_result_file_clicked"
        const val RESULT_REPOSITORY_CLICKED = "search_result_repository_clicked"
    }

    object ActivityFilterState {
        const val OPEN = "activity_filter_open"
        const val MERGED = "activity_filter_merged"
        const val DECLINED = "activity_filter_declined"
    }

    object TrackRepos {
        const val TRACK_REPOS_POPUP_OPENED = "track_repos_info_popup_opened"
        const val TRACK_REPOS_DO_NOT_SHOW_AGAIN_TAPPED = "track_repos_do_not_show_again_tapped"
        const val TRACK_REPOS_CANCEL_CLICKED = "track_repos_cancel_action"
        const val TRACK_REPOS_CONFIRM_CLICKED = "track_repos_confirm_action"
    }

    const val PR_ACTION = "pr_action"
    const val COMMENT_ACTION = "comment_action"
    const val SUMMARY_ACTION = "pr_summary_action"

    const val LOGIN_BUTTON_CLICKED = "login_button_clicked"
    const val LOGIN_CODE_RECEIVED = "login_code_received"
    const val LOGIN_SUCCESSFULLY = "login_successfully"
    const val LOGIN_FAILED = "login_failed"
    const val AUTH_FAILED = "auth_failed"

    const val PR_LINK_NAV_CONFIRM = "pr_link_confirm"
    const val PR_LINK_NAV_CANCEL = "pr_link_cancel"

    const val THEME_DIALOG_CLICKED = "theme_dialog_clicked"
    const val THEME_CHANGED = "theme_changed"
    const val AKVELON_LINK_CLICKED = "akvelon_link_clicked"
    const val LOGOUT = "logout"

    const val SWITCH_ACCOUNT_CANCEL = "SwitchAccountCancelled"

    const val EMAIL_COLLECTING_FAIL = "email_collecting_fail"

    const val PRIVACY_POLICY_ACCEPTED = "privacy_policy_accepted"

    const val NO_BROWSER_FOUND = "no_browser_found"
}
