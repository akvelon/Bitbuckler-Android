/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 December 2020
 */
package com.akvelon.bitbuckler.model.entity.repository

import com.akvelon.bitbuckler.model.entity.Content
import java.time.LocalDateTime

class Commit(

    val hash: String,

    val type: String,

    val date: LocalDateTime,

    val summary: Content?,

    val message: String,

    val author: CommitAuthor
)
