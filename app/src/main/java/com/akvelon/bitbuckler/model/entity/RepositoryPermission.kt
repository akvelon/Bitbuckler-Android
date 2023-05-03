/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 23 February 2022
 */

package com.akvelon.bitbuckler.model.entity

import com.akvelon.bitbuckler.model.entity.repository.Repository

data class RepositoryPermission(
    val type: String,
    val user: User,
    val permission: String,
    val repository: Repository,
)
