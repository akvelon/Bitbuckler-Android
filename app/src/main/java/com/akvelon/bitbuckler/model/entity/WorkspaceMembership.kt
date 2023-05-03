/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 23 February 2022
 */

package com.akvelon.bitbuckler.model.entity

data class WorkspaceMembership(

    val user: User,

    val workspace: Workspace
)
