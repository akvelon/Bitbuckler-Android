/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 07 March 2022
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val uuid: String,

    @SerializedName("app_version")
    val appVersion: String? = null,

    val emails: List<String>? = null
) : Parcelable
