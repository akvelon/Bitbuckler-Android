package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import com.akvelon.bitbuckler.model.entity.Links
import com.akvelon.bitbuckler.model.entity.User
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalAccount(

    val uuid: String,

    val username: String?,

    @SerializedName("display_name")
    val displayName: String,

    val nickname: String,

    @SerializedName("account_id")
    val accountId: String,

    val links: Links,

    val location: String?,

    @SerializedName("refresh_token")
    val refreshToken: String
) : Parcelable

fun LocalAccount.toUser() = User(
    uuid = uuid,
    username = username,
    displayName = displayName,
    nickname = nickname,
    accountId = accountId,
    links = links,
    location = location
)

fun User.toLocalAccount(refreshToken: String) = LocalAccount(
    uuid = uuid,
    username = username,
    displayName = displayName,
    nickname = nickname,
    accountId = accountId,
    links = links,
    location = location,
    refreshToken = refreshToken
)