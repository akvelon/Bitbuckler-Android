/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 12 May 2021
 */

package com.akvelon.bitbuckler.mock

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.adapter.LocalDateTimeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private const val LOCAL_URL = "http://localhost:8080/"

val mockNetworkModule = module(override = true) {
    single {
        bitApi(get(), get())
    }
    single {
        bitOkHttp()
    }
    single {
        gson()
    }
}

fun bitApi(client: OkHttpClient, gson: Gson): BitApi = Retrofit.Builder()
    .baseUrl(LOCAL_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
    .create(BitApi::class.java)

fun bitOkHttp() = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.SECONDS)
    .readTimeout(1, TimeUnit.SECONDS)
    .writeTimeout(1, TimeUnit.SECONDS)
    .build()

fun gson() = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
    .create()
