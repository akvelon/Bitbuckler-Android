/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 29 April 2021
 */

package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.Slugs
import kotlinx.parcelize.Parcelize

@Parcelize
class SourceArgs(

    val slugs: Slugs,

    var currentBranch: String,

    val folders: MutableList<String> = mutableListOf()
) : Parcelable
