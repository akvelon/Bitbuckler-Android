/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 December 2020
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.auth.AuthApi
import com.akvelon.bitbuckler.model.datasource.api.auth.response.TokenResponse
import com.akvelon.bitbuckler.model.datasource.local.EncryptedPrefs
import com.akvelon.bitbuckler.model.entity.GrantType
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepository(
    private val api: AuthApi,
    private val encryptedPrefs: EncryptedPrefs
) {

    private var logOutChannel = 0

    val onLogout: MutableStateFlow<Int> by lazy {
        MutableStateFlow(logOutChannel)
    }

    fun hasAccount() = encryptedPrefs.refreshToken != null

    suspend fun login(code: String): TokenResponse {
        return api.getAccessTokenByCode(
            GrantType.authorizationCode,
            code
        ).apply {
            encryptedPrefs.accessToken = accessToken
            encryptedPrefs.refreshToken = refreshToken
        }
    }

    fun getAccessToken() = encryptedPrefs.accessToken

    fun getRefreshToken() = encryptedPrefs.refreshToken

    suspend fun updateAccessTokenByRefreshToken() : TokenResponse? {
        val token = getRefreshToken()
        return if(token == null) null else {
            api.getAccessToken(
                GrantType.refreshToken,
                token
            ).apply {
                encryptedPrefs.accessToken = accessToken
                encryptedPrefs.refreshToken = refreshToken
            }
        }
    }

    suspend fun logout() {
        safeLogout()
        onLogout.emit(++logOutChannel)
    }

    fun setRefreshToken(refreshToken: String) {
        encryptedPrefs.refreshToken = refreshToken
    }

    fun safeLogout() {
        encryptedPrefs.accessToken = null
        encryptedPrefs.refreshToken = null
        encryptedPrefs.currentAccount = null
        encryptedPrefs.authenticatedEmails = null
    }
}
