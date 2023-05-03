/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 December 2020
 */

package com.akvelon.bitbuckler.model.datasource.api.auth.response

import com.google.gson.annotations.SerializedName

class TokenResponse(
    val scopes: String,

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("expires_in")
    val expiresIn: String,

    @SerializedName("token_type")
    val tokenType: String,

    val state: String,

    @SerializedName("refresh_token")
    val refreshToken: String
)
