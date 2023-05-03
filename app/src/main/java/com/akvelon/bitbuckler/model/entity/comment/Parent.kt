/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 January 2021
 */

package com.akvelon.bitbuckler.model.entity.comment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Parent(
    val id: Int
) : Parcelable
