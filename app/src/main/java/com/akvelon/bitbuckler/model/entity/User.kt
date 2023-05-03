/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 December 2020
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    val uuid: String,

    val username: String?,

    @SerializedName("display_name")
    val displayName: String,

    val nickname: String,

    @SerializedName("account_id")
    val accountId: String,

    val links: Links,

    val location: String?
) : Parcelable
