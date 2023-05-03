/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 25 February 2022
 */

package com.akvelon.bitbuckler.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class WorkspaceSize(

    @PrimaryKey
    val workspaceSlug: String,

    val size: Int,

    val lastUpdate: LocalDateTime
)
