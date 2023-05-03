/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 03 December 2020
 */

package com.akvelon.bitbuckler.model.datasource.api.auth

import com.akvelon.bitbuckler.model.datasource.api.auth.response.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("access_token")
    suspend fun getAccessTokenByCode(
        @Field("grant_type") grantType: String,
        @Field("code") code: String
    ): TokenResponse

    @FormUrlEncoded
    @POST("access_token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String
    ): TokenResponse
}
