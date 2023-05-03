/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 01 December 2020
 */

package com.akvelon.bitbuckler.model.datasource.api.bit.interceptor

import com.akvelon.bitbuckler.model.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthHeaderInterceptor(
    private val authRepository: AuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        authRepository.getAccessToken()?.let {
            request = setAuthHeader(request, it)
        }

        return chain.proceed(request)
    }

    private fun setAuthHeader(request: Request, accessToken: String) =
        request.newBuilder().addHeader("Authorization", "Bearer $accessToken").build()
}
