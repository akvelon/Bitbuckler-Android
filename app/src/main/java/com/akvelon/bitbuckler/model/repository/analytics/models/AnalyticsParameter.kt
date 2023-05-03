/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 12 March 2021
 */

package com.akvelon.bitbuckler.model.repository.analytics.models

object AnalyticsParameter {
    object PrAction {
        const val MERGED = "merged"
        const val DECLINED = "declined"
        const val APPROVED = "approved"
        const val APPROVE_REVOKED = "approve_revoked"
        const val CHANGES_REQUESTED = "changes_requested"
        const val CHANGES_REQUEST_REVOKED = "changes_request_revoked"
    }

    object Comment {
        const val ADDED = "added"
        const val EDITED = "edited"
        const val DELETED = "deleted"
        const val INLINE_ADDED = "inline_added"
        const val INLINE_EDITED = "inline_edited"
        const val INLINE_DELETED = "inline_deleted"
    }

    object PrDetailsOrigin {
        const val ACTIVITY = "activity"
        const val REPO_DETAILS = "repo_details"
    }

    object CommitDetailsOrigin {
        const val REPO_DETAILS = "repo_details"
        const val PR_DETAILS = "pr_details"
    }

    object Summary {
        const val COLLAPSED = "collapsed"
        const val EXPANDED = "expanded"
    }

    object Theme {
        const val LIGHT = "light"
        const val DARK = "dark"
        const val DEFAULT = "default"
    }
}
