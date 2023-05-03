/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Entity
@Parcelize
data class Workspace(

    @PrimaryKey
    val uuid: String,

    val name: String,

    val slug: String,

    @SerializedName("is_private")
    val isPrivate: Boolean,

    val links: Links,

    @SerializedName("created_on")
    val createdOn: LocalDateTime
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Workspace

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}
