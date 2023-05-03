/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 05 May 2021
 */
package com.akvelon.bitbuckler.model.entity.args

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.Slugs
import kotlinx.parcelize.Parcelize

@Parcelize
class SourceDetailsArgs(
    val slugs: Slugs,

    val commitHash: String,

    val path: String
) : Parcelable {

    val fileName
        get() = path.split("/").last()
}
