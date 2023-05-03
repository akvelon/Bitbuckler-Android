/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 December 2020
 */

package com.akvelon.bitbuckler.model.entity.repository

class RepositoryState(

    val commit: Commit,

    val repository: Repository,

    val branch: Branch
)
