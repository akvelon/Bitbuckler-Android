/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 20 November 2020
 */

package com.akvelon.bitbuckler.di

import com.akvelon.bitbuckler.BuildConfig
import com.akvelon.bitbuckler.model.datasource.api.auth.AuthApi
import com.akvelon.bitbuckler.model.datasource.api.auth.interceptor.BasicAuthInterceptor
import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.adapter.LocalDateTimeAdapter
import com.akvelon.bitbuckler.model.datasource.api.bit.authenticator.AuthInterceptor
import com.akvelon.bitbuckler.model.datasource.api.bit.interceptor.AuthHeaderInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

private const val BIT_QUALIFIER = "bit"
private const val AUTH_QUALIFIER = "auth"

val networkModule = module {

    single {
        bitApi(get(named(BIT_QUALIFIER)), get())
    }

    single(named(BIT_QUALIFIER)) {
        bitOkHttp(get(), get(), get())
    }

    single {
        authApi(get(named(AUTH_QUALIFIER)), get())
    }

    single(named(AUTH_QUALIFIER)) {
        authOkHttp(get(), get())
    }

    single {
        gson()
    }

    single {
        loggingInterceptor()
    }

    single {
        basicAuthInterceptor()
    }

    single {
        AuthHeaderInterceptor(get())
    }

    single {
        AuthInterceptor(get(), get())
    }
}

fun bitApi(client: OkHttpClient, gson: Gson): BitApi =
    Retrofit.Builder()
        .baseUrl(BuildConfig.BITBUCKET_API)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(BitApi::class.java)

fun authApi(client: OkHttpClient, gson: Gson): AuthApi =
    Retrofit.Builder()
        .baseUrl(BuildConfig.BITBUBCKET_AUTH_API)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(AuthApi::class.java)

fun gson() = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
    .create()

fun bitOkHttp(
    loggingInterceptor: HttpLoggingInterceptor,
    authHeaderInterceptor: AuthHeaderInterceptor,
    authInterceptor: AuthInterceptor
): OkHttpClient {
    val dispatcher = Dispatcher()
    dispatcher.maxRequests = 70
    return OkHttpClient().newBuilder()
        .addInterceptor(authHeaderInterceptor)
        .addInterceptor(authInterceptor)
        .dispatcher(dispatcher)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(loggingInterceptor)
            }
        }
        .build()
}

fun authOkHttp(
    loggingInterceptor: HttpLoggingInterceptor,
    authInterceptor: BasicAuthInterceptor
) =
    OkHttpClient().newBuilder()
        .addInterceptor(authInterceptor)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(loggingInterceptor)
            }
        }
        .build()

fun loggingInterceptor() = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BASIC
}

fun basicAuthInterceptor() = BasicAuthInterceptor()
