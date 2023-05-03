/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 December 2020
 */

package com.akvelon.bitbuckler.model.datasource.api.auth.interceptor

import com.akvelon.bitbuckler.BuildConfig
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader(
                "Authorization",
                Credentials.basic(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)
            )
            .build()
        return chain.proceed(request)
    }
}
