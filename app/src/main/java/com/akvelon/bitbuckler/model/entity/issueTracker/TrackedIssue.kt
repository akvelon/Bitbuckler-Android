package com.akvelon.bitbuckler.model.entity.issueTracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueKind
import com.akvelon.bitbuckler.model.entity.repository.issue.IssuePriority
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueState
import java.time.LocalDateTime

@Entity(tableName = "tracked_issue")
class TrackedIssue(
    @PrimaryKey val id: Int,

    val repoUuid: String? = null,

    val workspaceSlug: String,

    val repositorySlug: String,

    val priority: IssuePriority,

    val kind: IssueKind,

    val state: IssueState,

    val title: String,

    val watches: Int,

    val votes: Int,

    val reporter: User,

    val assignee: User?,

    val content: Content,

    val createdOn: LocalDateTime,

    val updatedOn: LocalDateTime,

    val commentCount: Int,

    ) {
    constructor(issue: Issue, repoUuid: String, workspaceSlug: String, repositorySlug: String, commentCount: Int) : this(
        id = issue.id,
        repoUuid = repoUuid,
        workspaceSlug = workspaceSlug,
        repositorySlug = repositorySlug,
        priority = issue.priority,
        kind = issue.kind,
        state = issue.state,
        title = issue.title,
        watches = issue.watches,
        votes = issue.votes,
        reporter = issue.reporter,
        assignee = issue.assignee,
        content = issue.content,
        createdOn = issue.createdOn,
        updatedOn = issue.updatedOn,
        commentCount = commentCount
    )
}