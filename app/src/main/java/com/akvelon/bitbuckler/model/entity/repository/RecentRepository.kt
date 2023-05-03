/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 18 February 2021
 */

package com.akvelon.bitbuckler.model.entity.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akvelon.bitbuckler.model.entity.Links
import java.time.LocalDateTime

@Entity
data class RecentRepository(

    @PrimaryKey
    val uuid: String,

    val name: String,

    val isPrivate: Boolean,

    val slug: String,

    val workspaceSlug: String,

    var lastEntrance: LocalDateTime,

    val links: Links
)
