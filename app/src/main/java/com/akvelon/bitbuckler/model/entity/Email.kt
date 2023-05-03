/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 2 February 2022
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Email(

    @SerializedName("is_primary")
    val isPrimary: Boolean,

    @SerializedName("is_confirmed")
    val isConfirmed: Boolean,

    val email: String,
) : Parcelable
