package com.akvelon.bitbuckler.model.datasource.api.bit.authenticator

import com.akvelon.bitbuckler.common.AppFlags
import com.akvelon.bitbuckler.model.entity.REQUEST_AUTHENTICATION_FAILED
import com.akvelon.bitbuckler.model.repository.AuthRepository
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsEvent
import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.HttpException

class AuthInterceptor(
    private val analytics: AnalyticsProvider,
    private val authRepository: AuthRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (AppFlags.enableAuthInterceptor) {

            val request = chain.request()

            val response = chain.proceed(request.addAuthHeader())

            return if (response.code == REQUEST_AUTHENTICATION_FAILED) {

                if (response.body != null)
                    response.close()

                updateAccessToken()

                analytics.report(AnalyticsEvent.Login.RefreshTokenExpired)

                chain.proceed(request.addAuthHeader())
            } else {
                response
            }
        } else {
            return chain.proceed(chain.request())
        }
    }

    private fun updateAccessToken() {
        runBlocking(Dispatchers.IO) {
            try {
                authRepository.updateAccessTokenByRefreshToken()
            } catch (ex: Throwable) {
                try {
                    authRepository.updateAccessTokenByRefreshToken()
                } catch (ex: HttpException) {
                    authRepository.logout()
                } catch (ex: Exception) {

                }
            }
        }
    }

    private fun Request.addAuthHeader(): Request {
        val accessToken = authRepository.getAccessToken()
        return this
            .newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}
