/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 December 2020
 */

package com.akvelon.bitbuckler.model.entity.pullrequest

import com.akvelon.bitbuckler.model.entity.Content
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.participant.Participant
import com.akvelon.bitbuckler.model.entity.repository.RepositoryState
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Suppress("LongParameterList")
open class PullRequest(
    val id: Int,

    val title: String,

    @SerializedName("comment_count")
    val commentCount: Int,

    val state: PullRequestState,

    val source: RepositoryState,

    val destination: RepositoryState,

    val summary: Content?,

    val participants: List<Participant>?,

    val author: User,

    @SerializedName("close_source_branch")
    val closeSourceBranch: Boolean,

    @SerializedName("closed_by")
    val closedBy: User?,

    @SerializedName("created_on")
    val createdOn: LocalDateTime,

    @SerializedName("updated_on")
    val updatedOn: LocalDateTime,
) {

    var type: PullRequestType = PullRequestType.UNKNOWN

    val workspaceSlug
        get() = destination.repository.fullName.split("/").first()

    val repositorySlug
        get() = destination.repository.fullName.split("/").last()

    val isOpen
        get() = state == PullRequestState.OPEN

    val reviewers
        get() = participants
            ?.filter { participant ->
                participant.user.uuid != author.uuid && participant.isReviewer
            }
            ?.sortedBy {
                it.stateOrder
            }

    val sourceBranch
        get() =
            if (sourceIsEqualDestination()) {
                source.branch.name
            } else {
                "${source.repository.fullName}:${source.branch.name}"
            }

    val destinationBranch
        get() =
            if (sourceIsEqualDestination()) {
                destination.branch.name
            } else {
                "${destination.repository.fullName}:${destination.branch.name}"
            }

    fun toTracked(
        repoUuid: String = "",
        slugs: Slugs,
    ) =
        TrackedPullRequest(
            repoUuid = repoUuid,
            workspaceSlug = slugs.workspace,
            repositorySlug = slugs.repository,
            idInRepository = this.id,
            title = this.title,
            state = this.state,
            authorDisplayName = this.author.displayName,
            authorAvatar = this.author.links.avatar.href,
            commentCount = this.commentCount,
            createdOn = this.createdOn,
            updatedOn = this.updatedOn,
            type = this.type
        )

    fun toTracked(
        repoUuid: String = "",
        slugs: Slugs,
        user: User,
    ) =
        TrackedPullRequest(
            repoUuid = repoUuid,
            workspaceSlug = slugs.workspace,
            repositorySlug = slugs.repository,
            idInRepository = this.id,
            title = this.title,
            state = this.state,
            authorDisplayName = this.author.displayName,
            authorAvatar = this.author.links.avatar.href,
            commentCount = this.commentCount,
            createdOn = this.createdOn,
            updatedOn = this.updatedOn,
            type = if (this.reviewers?.firstOrNull { it.user.uuid == user.uuid } != null) {
                PullRequestType.I_AM_REVIEWING
            } else {
                PullRequestType.UNKNOWN
            }
        )

    fun findParticipantByUuid(uuid: String) =
        participants?.find { participant -> participant.user.uuid == uuid }

    private fun sourceIsEqualDestination() = source.repository.uuid == destination.repository.uuid
}
