/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 21 March 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class IssueArgs(

    val workspaceSlug: String,

    val repositorySlug: String,

    val id: Int
) : Parcelable
