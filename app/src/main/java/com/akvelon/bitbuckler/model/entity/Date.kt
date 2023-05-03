/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 10 March 2022
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Date(

    val year: Int? = null,

    val month: Int? = null,

    @SerializedName("day_of_month")
    val dayOfMonth: Int? = null,

    val hour: Int? = null,

    val minute: Int? = null

) : Parcelable {
    constructor() : this(null)
}
