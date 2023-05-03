/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 December 2020
 */

package com.akvelon.bitbuckler.model.entity.participant

import com.akvelon.bitbuckler.model.entity.User
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class Participant(

    val state: ParticipantState?,

    val role: ParticipantRole,

    val user: User,

    val approved: Boolean,

    @SerializedName("participated_on")
    val participatedOn: LocalDateTime?
) {

    val stateOrder
        get() = when (state) {
            ParticipantState.APPROVED -> 0
            ParticipantState.CHANGES_REQUESTED -> 1
            null -> 2
        }

    val isReviewer
        get() = role == ParticipantRole.PARTICIPANT && state != null || role == ParticipantRole.REVIEWER
}
