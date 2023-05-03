package com.akvelon.bitbuckler.domain.ads

interface AdsUseCase {

    suspend fun initAds()

    suspend fun areAdsEnabled(): Boolean

    suspend fun areAdsInitialized(): Boolean

    suspend fun setAdsInitialized(flag: Boolean)
}