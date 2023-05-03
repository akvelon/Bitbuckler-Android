/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 10 February 2021
 */

package com.akvelon.bitbuckler.model.entity.participant

import com.google.gson.annotations.SerializedName

enum class ParticipantState {

    @SerializedName("approved")
    APPROVED,

    @SerializedName("changes_requested")
    CHANGES_REQUESTED
}
