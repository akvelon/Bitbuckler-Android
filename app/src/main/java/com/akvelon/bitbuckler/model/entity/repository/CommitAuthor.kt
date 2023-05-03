/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 May 2021
 */

package com.akvelon.bitbuckler.model.entity.repository

import com.akvelon.bitbuckler.model.entity.User

class CommitAuthor(
    val raw: String,

    val user: User?
)
