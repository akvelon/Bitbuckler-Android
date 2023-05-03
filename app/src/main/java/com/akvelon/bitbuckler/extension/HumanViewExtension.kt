/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 22 December 2020
 */

@file:Suppress("TooManyFunctions")

package com.akvelon.bitbuckler.extension

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.Language
import com.akvelon.bitbuckler.model.entity.TimePeriod
import com.akvelon.bitbuckler.model.entity.participant.Participant
import com.akvelon.bitbuckler.model.entity.participant.ParticipantState
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FilePathHolder
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileStatus
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.AddedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.ConflictedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.RemovedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.UnchangedLine
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueKind
import com.akvelon.bitbuckler.model.entity.repository.issue.IssuePriority
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueState
import com.akvelon.bitbuckler.model.entity.source.Source
import com.akvelon.bitbuckler.model.entity.source.SourceType
import com.akvelon.bitbuckler.model.entity.statuses.Status
import com.akvelon.bitbuckler.model.entity.statuses.StatusState
import java.io.IOException
import kotlin.math.round

@DrawableRes
fun Participant.getStateBorder() = when (this.state) {
    ParticipantState.APPROVED -> R.drawable.bgr_reviewer_approved
    ParticipantState.CHANGES_REQUESTED -> R.drawable.bgr_reviewer_changes
    null -> R.drawable.bgr_reviewer_no_status
}

@StringRes
fun PullRequestState.getStringRes() = when (this) {
    PullRequestState.OPEN -> R.string.pr_state_open
    PullRequestState.MERGED -> R.string.pr_state_merged
    PullRequestState.DECLINED -> R.string.pr_state_declined
    PullRequestState.SUPERSEDED -> R.string.pr_state_superseded
}

@DrawableRes
fun PullRequestState.getStateBackground() = when (this) {
    PullRequestState.MERGED -> R.drawable.bgr_pr_state_merged
    PullRequestState.OPEN -> R.drawable.bgr_pr_state_open
    PullRequestState.DECLINED -> R.drawable.bgr_pr_state_declined
    PullRequestState.SUPERSEDED -> R.drawable.bgr_pr_state_superseded
}

@ColorRes
fun PullRequestState.getStateTextColor() = when (this) {
    PullRequestState.MERGED -> R.color.mergedStateTextColor
    PullRequestState.OPEN -> R.color.openStateTextColor
    PullRequestState.DECLINED -> R.color.declinedStateTextColor
    PullRequestState.SUPERSEDED -> R.color.supersededStateTextColor
}

@ColorRes
fun PullRequestState.getAccentColor() = when (this) {
    PullRequestState.MERGED -> R.color.mergedStateAccentColor
    PullRequestState.OPEN -> R.color.openStateAccentColor
    PullRequestState.DECLINED -> R.color.declinedStateAccentColor
    PullRequestState.SUPERSEDED -> R.color.supersededStateAccentColor
}

@DrawableRes
fun Status.getStateIcon() = when (state) {
    StatusState.SUCCESSFUL -> R.drawable.ic_build_successful
    StatusState.FAILED -> R.drawable.ic_build_failed
    StatusState.STOPPED -> R.drawable.ic_build_stopped
    StatusState.INPROGRESS -> R.drawable.ic_build_in_progress
}

fun Content?.isNullOrEmpty() =
    if (this != null) {
        val raw = this.raw
            .removePrefix(Content.ZERO_WIDTH.toString())
            .removeSuffix(Content.ZERO_WIDTH.toString())
            .replace(Content.EMPTY_RAW, "")

        raw.isEmpty()
    } else {
        true
    }

fun Content.getRenderedContent(): CharSequence {
    val raw = this.raw
        .removePrefix(Content.ZERO_WIDTH.toString())
        .removeSuffix(Content.ZERO_WIDTH.toString())
        .replace(Content.EMPTY_RAW, "")

    return if (raw.isNotEmpty()) {
        HtmlCompat.fromHtml(
            this.html,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).trim()
    } else {
        raw
    }
}

@DrawableRes
fun FileStatus.getStatusIcon() = when (this) {
    FileStatus.REMOVED -> R.drawable.ic_file_removed
    FileStatus.ADDED -> R.drawable.ic_file_added
    FileStatus.MODIFIED, FileStatus.MERGE_CONFLICT, FileStatus.REMOTE_DELETED ->
        R.drawable.ic_file_modified
    FileStatus.RENAMED -> R.drawable.ic_file_renamed
}

fun FilePathHolder.getFormattingPath() =
    filePath.split("/").let {
        buildSpannedString {
            append(
                it.dropLast(1).joinToString(separator = " / ", postfix = " / ")
            )
            bold {
                append(
                    it.last()
                )
            }
        }
    }

@DrawableRes
fun LineDiff.getLineBackground() = when (this) {
    is AddedLine ->
        if (isConflicted) R.drawable.bgr_line_conflicted_section else R.drawable.bgr_line_added
    is RemovedLine ->
        if (isConflicted) R.drawable.bgr_line_conflicted_section else R.drawable.bgr_line_removed
    is UnchangedLine ->
        if (isConflicted) R.drawable.bgr_line_conflicted_section else R.drawable.bgr_line_unchanged
    is ConflictedLine -> R.drawable.bgr_line_conflicted
    else -> R.drawable.bgr_line_collapsed
}

@StringRes
fun LineDiff.getPrefix() = when (this) {
    is AddedLine, is ConflictedLine -> R.string.line_added_prefix
    is RemovedLine -> R.string.line_removed_prefix
    is UnchangedLine -> R.string.line_unchanged_prefix
    else -> null
}

@ColorRes
fun String.getLanguageTint() = when (this) {
    Language.PYTHON.kind -> R.color.python
    Language.ANDROID.kind -> R.color.android
    Language.C_PLUS.kind -> R.color.cPlus
    Language.C_SHARP.kind -> R.color.cSharp
    Language.C.kind -> R.color.c
    Language.KOTLIN.kind -> R.color.kotlin
    Language.JAVA.kind -> R.color.java
    Language.JAVASCRIPT.kind -> R.color.javascript
    Language.GO.kind -> R.color.go
    Language.RUBY.kind -> R.color.ruby
    Language.SCALA.kind -> R.color.scala
    Language.DART.kind -> R.color.dart
    else -> R.color.others
}

fun Long.toHumanBytesCount(): String {
    val zero = 0L
    val kilobyte = 1024L
    val megabyte = 1048576L
    val gigabyte = 1073741824L

    return when (this) {
        in zero..kilobyte -> "$this B"
        in kilobyte..megabyte -> "${round(this divide kilobyte).toInt()} KB"
        in megabyte..gigabyte -> "${round(this divide megabyte).toInt()} MB"
        else -> "${(this divide gigabyte).roundToTenth()} GB"
    }
}

@StringRes
fun TimePeriod.getStringRes(short: Boolean) =
    if (short) {
        when (this) {
            TimePeriod.DAYS -> R.string.days_ago_short
            TimePeriod.HOURS -> R.string.hours_ago_short
            TimePeriod.MINUTES -> R.string.minutes_ago_short
            TimePeriod.NOW -> R.string.now
        }
    } else {
        when (this) {
            TimePeriod.DAYS -> R.string.days_ago
            TimePeriod.HOURS -> R.string.hours_ago
            TimePeriod.MINUTES -> R.string.minutes_ago
            TimePeriod.NOW -> R.string.just_now
        }
    }

@DrawableRes
fun Source.getIcon() = when (type) {
    SourceType.COMMIT_DIRECTORY -> R.drawable.ic_folder
    SourceType.COMMIT_FILE -> R.drawable.ic_file
}

@DrawableRes
fun IssuePriority.getPriorityIcon() = when (this) {
    IssuePriority.TRIVIAL -> R.drawable.ic_arrow_down
    IssuePriority.MINOR -> R.drawable.ic_double_arrow_down
    IssuePriority.MAJOR -> R.drawable.ic_double_arrow_up
    IssuePriority.CRITICAL -> R.drawable.ic_arrow_up
    IssuePriority.BLOCKER -> R.drawable.ic_blocker
}

@ColorRes
fun IssuePriority.getPriorityColor() = when (this) {
    IssuePriority.TRIVIAL -> R.color.onHoldState
    IssuePriority.MINOR -> R.color.closedState
    IssuePriority.MAJOR, IssuePriority.CRITICAL, IssuePriority.BLOCKER -> R.color.invalidState
}

@DrawableRes
fun IssuePriority.getPriorityBackgroundOutlined() = when (this) {
    IssuePriority.TRIVIAL -> R.drawable.bgr_issue_state_on_hold
    IssuePriority.MINOR -> R.drawable.bgr_issue_state_closed
    IssuePriority.MAJOR, IssuePriority.CRITICAL, IssuePriority.BLOCKER
    -> R.drawable.bgr_issue_state_invalid
}

@ColorRes
fun IssueState.getStateColor() = when (this) {
    IssueState.NEW -> R.color.newState
    IssueState.OPEN -> R.color.openStateTextColor
    IssueState.CLOSED, IssueState.RESOLVED -> R.color.closedState
    IssueState.ON_HOLD -> R.color.onHoldState
    IssueState.INVALID, IssueState.WONTFIX -> R.color.invalidState
    IssueState.DUPLICATE -> R.color.duplicateState
}

@DrawableRes
fun IssueState.getStateBackground() = when (this) {
    IssueState.NEW -> R.drawable.bgr_issue_state_new
    IssueState.OPEN -> R.drawable.bgr_issue_state_open
    IssueState.CLOSED, IssueState.RESOLVED -> R.drawable.bgr_issue_state_closed
    IssueState.ON_HOLD -> R.drawable.bgr_issue_state_on_hold
    IssueState.INVALID, IssueState.WONTFIX -> R.drawable.bgr_issue_state_invalid
    IssueState.DUPLICATE -> R.drawable.bgr_issue_state_duplicate
}

@ColorRes
fun IssueKind.getKindIcon() = when (this) {
    IssueKind.BUG -> R.drawable.is_issue_bug
    IssueKind.ENHANCEMENT -> R.drawable.is_issue_enhancement
    IssueKind.PROPOSAL -> R.drawable.is_issue_proposal
    IssueKind.TASK -> R.drawable.is_issue_task
}

@ColorRes
fun IssueKind.getKindColor() = when (this) {
    IssueKind.BUG -> R.color.invalidState
    IssueKind.ENHANCEMENT -> R.color.closedState
    IssueKind.PROPOSAL -> R.color.duplicateState
    IssueKind.TASK -> R.color.openStateTextColor
}

@DrawableRes
fun IssueKind.getKindBackgroundOutlined() = when (this) {
    IssueKind.BUG -> R.drawable.bgr_issue_state_invalid
    IssueKind.ENHANCEMENT -> R.drawable.bgr_issue_state_closed
    IssueKind.PROPOSAL -> R.drawable.bgr_issue_state_duplicate
    IssueKind.TASK -> R.drawable.bgr_issue_state_open
}

@StringRes
fun Throwable.getHumanReadableMessageRes() = when (this) {
    is IOException -> R.string.error_no_internet
    else -> R.string.error_unknown
}

@StringRes
fun Repository.getIsPrivateTitle() =
    if (isPrivate) R.string.private_repository else R.string.public_repository

@DrawableRes
fun Repository.getIsPrivateIconId() =
    if (isPrivate) R.drawable.ic_lock_repo else R.drawable.ic_unlocked
